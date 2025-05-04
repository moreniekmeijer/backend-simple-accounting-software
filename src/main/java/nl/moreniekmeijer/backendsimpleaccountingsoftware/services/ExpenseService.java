package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ParsedReceiptDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.detectors.*;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.ExpenseMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ExpenseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class ExpenseService {

    @Value("${ocr.api.key}")
    private String apiKey;

    private final ExpenseRepository expenseRepository;

    private final VendorDetector vendorDetector = new VendorDetector();
    private final InvoiceNumberDetector invoiceNumberDetector = new InvoiceNumberDetector();
    private final DateDetector dateDetector = new DateDetector();
    private final AmountDetector amountDetector = new AmountDetector();
    private final VatDetector vatDetector = new VatDetector();

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public ParsedReceiptDto parseReceipt(MultipartFile file) throws Exception {
        String ocrText = runOcrApi(file);
        return extractDataFromText(ocrText);
    }

    public Expense saveParsedReceipt(ParsedReceiptDto dto, MultipartFile file) throws IOException {
        Expense expense = ExpenseMapper.fromParsedDto(dto, file);
        return expenseRepository.save(expense);
    }

    public Optional<Expense> updateExpense(Long id, Expense updatedExpense) {
        return expenseRepository.findById(id).map(existing -> {
            ExpenseMapper.updateExpenseFields(existing, updatedExpense);
            return expenseRepository.save(existing);
        });
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    private ParsedReceiptDto extractDataFromText(String text) {
        String[] lines = text.split("\\r?\\n");
        return new ParsedReceiptDto(
                vendorDetector.detectVendor(lines),
                invoiceNumberDetector.detectInvoiceNumber(lines),
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
