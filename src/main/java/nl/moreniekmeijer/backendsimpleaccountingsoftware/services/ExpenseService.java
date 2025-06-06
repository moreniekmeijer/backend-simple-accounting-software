package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.transaction.Transactional;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.*;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors.*;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.ExpenseMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.InvestmentMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvestmentDetails;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ExpenseRepository;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.InvestmentRepository;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.utils.GoogleDriveUploader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExpenseService {

    @Value("${ocr.api.key}")
    private String apiKey;

    private final ExpenseRepository expenseRepository;
    private final InvestmentRepository investmentRepository;
    private final GoogleDriveUploader googleDriveUploader;

    private final VendorDetector vendorDetector = new VendorDetector();
    private final DateDetector dateDetector = new DateDetector();
    private final AmountDetector amountDetector = new AmountDetector();
    private final VatDetector vatDetector = new VatDetector();

    public ExpenseService(ExpenseRepository expenseRepository, InvestmentRepository investmentRepository, GoogleDriveUploader googleDriveUploader) {
        this.expenseRepository = expenseRepository;
        this.investmentRepository = investmentRepository;
        this.googleDriveUploader = googleDriveUploader;
    }

    public ExpenseDto parseReceiptToDto(MultipartFile file) throws Exception {
        String ocrText = runOcrApi(file);
        ParsedReceiptDto parsed = extractDataFromText(ocrText);

        String invoiceNumber = Objects.requireNonNull(file.getOriginalFilename()).replaceFirst("\\.pdf$", "");
        parsed.setInvoiceNumber(invoiceNumber);

        return ExpenseMapper.fromParsedDto(parsed);
    }

    @Transactional
    public ExpenseOutputDto saveExpense(ExpenseDto dto, MultipartFile file) {
        Expense expense = ExpenseMapper.fromDto(dto);

        try {
            int year = dto.getDate().getYear();
            String driveUrl = googleDriveUploader.uploadToYearFolder(file, year);
            expense.setDriveUrl(driveUrl);
        } catch (IOException e) {
            throw new RuntimeException("Upload naar Google Drive mislukt", e);
        }

        if (expense.getAmount().compareTo(BigDecimal.valueOf(450)) > 0) {
            expense.setCategory("investering");
        }

        Expense saved = expenseRepository.save(expense);

        return ExpenseMapper.toResponseDto(saved);
    }

    @Transactional
    public void completeInvestment(Long expenseId, InvestmentInputDto dto) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense met ID " + expenseId + " niet gevonden"));

        if (expense.getInvestmentDetails() != null) {
            throw new IllegalStateException("Er is al een investering gekoppeld aan deze expense");
        }

        InvestmentDetails details = InvestmentMapper.toEntity(expense, dto);
        InvestmentDetails saved = investmentRepository.save(details);

        expense.setInvestmentDetails(saved);
        expenseRepository.save(expense);
    }

    @Transactional
    public ExpenseOutputDto saveExpenseAndCompleteInvestment(ExpenseDto expenseDto, MultipartFile file, InvestmentInputDto investmentDto) {
        ExpenseOutputDto savedExpense = saveExpense(expenseDto, file);

        if ("investering".equalsIgnoreCase(savedExpense.getCategory())) {
            completeInvestment(savedExpense.getId(), investmentDto);
        }

        return savedExpense;
    }

    public Optional<ExpenseOutputDto> updateExpense(Long id, ExpenseDto dto) {
        return expenseRepository.findById(id)
                .map(existing -> {
                    ExpenseMapper.updateExpenseFields(existing, ExpenseMapper.fromDto(dto));
                    return expenseRepository.save(existing);
                })
                .map(ExpenseMapper::toResponseDto);
    }

    public Optional<ExpenseOutputDto> getExpenseOutputById(Long id) {
        return expenseRepository.findById(id)
                .map(ExpenseMapper::toResponseDto);
    }

    public List<ExpenseOutputDto> getAllExpenses(Integer year) {
        if (year == null) {
            return expenseRepository.findAll().stream()
                    .map(ExpenseMapper::toResponseDto)
                    .toList();
        }
        return getAllExpensesByYear(year);
    }

    public List<ExpenseOutputDto> getAllExpensesByYear(Integer year) {
        LocalDate onDate = LocalDate.of(year, 12, 31);

        return expenseRepository.findAll().stream()
                .filter(expense -> isRelevantForYear(expense, year))
                .map(expense -> mapExpenseToDtoWithYearlyBookValue(expense, onDate))
                .toList();
    }

    private boolean isRelevantForYear(Expense expense, int year) {
        if (!"investering".equalsIgnoreCase(expense.getCategory())) {
            return expense.getDate().getYear() == year;
        }
        InvestmentDetails details = expense.getInvestmentDetails();
        int startYear = details.getPurchaseDate().getYear();
        int endYear = startYear + details.getDepreciationYears() - 1;
        return year >= startYear && year <= endYear;
    }

    private ExpenseOutputDto mapExpenseToDtoWithYearlyBookValue(Expense expense, LocalDate onDate) {
        ExpenseOutputDto dto = ExpenseMapper.toResponseDto(expense);
        if ("investering".equalsIgnoreCase(expense.getCategory())) {
            InvestmentDetails details = expense.getInvestmentDetails();
            dto.getInvestmentDetails().setAnnualDepreciation(details.getAnnualDepreciation());
            dto.getInvestmentDetails().setBookValue(details.getBookValue(onDate));
        }
        return dto;
    }

    public void deleteExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));

        try {
            googleDriveUploader.deleteFileById(expense.getDriveUrl());
        } catch (IOException e) {
            throw new RuntimeException("Fout bij het verwijderen van het bestand van Drive", e);
        }

        expenseRepository.delete(expense);
    }

    private ParsedReceiptDto extractDataFromText(String text) {
        String[] lines = text.split("\\r?\\n");
        return new ParsedReceiptDto(
                vendorDetector.detectVendor(lines),
                null,
                dateDetector.detectDate(lines),
                amountDetector.detectTotalAmount(lines),
                vatDetector.detectVat(lines)
        );
    }

    private String runOcrApi(MultipartFile file) throws Exception {
        URL url = new URL("https://api.ocr.space/parse/image");
        String boundary = Long.toHexString(System.currentTimeMillis());
        String CRLF = "\r\n";

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("apikey", apiKey);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        try (
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8), true)
        ) {
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"")
                    .append(file.getOriginalFilename()).append("\"").append(CRLF);
            writer.append("Content-Type: ").append(file.getContentType()).append(CRLF);
            writer.append(CRLF).flush();
            output.write(file.getBytes());
            output.flush();
            writer.append(CRLF).flush();

            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"language\"").append(CRLF);
            writer.append(CRLF).append("eng").append(CRLF).flush();

            writer.append("--").append(boundary).append("--").append(CRLF).flush();
        }

        StringBuilder response = new StringBuilder();
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            while (scanner.hasNextLine()) {
                response.append(scanner.nextLine());
            }
        }

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        return json.getAsJsonArray("ParsedResults")
                .get(0).getAsJsonObject()
                .get("ParsedText").getAsString();
    }
}