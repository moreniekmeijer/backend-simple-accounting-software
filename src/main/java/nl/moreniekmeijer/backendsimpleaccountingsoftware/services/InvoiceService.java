package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import jakarta.transaction.Transactional;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.InvoiceMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.CompanyDetails;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Invoice;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvoiceLine;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ClientRepository;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.utils.GoogleDriveUploader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.InvoiceRepository;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import static nl.moreniekmeijer.backendsimpleaccountingsoftware.utils.InvoiceUtils.*;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;
    private final GoogleDriveUploader googleDriveUploader;
    private final CompanyDetailsService companyDetailsService;

    public InvoiceService(InvoiceRepository invoiceRepository, ClientRepository clientRepository, GoogleDriveUploader googleDriveUploader, CompanyDetailsService companyDetailsService) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
        this.googleDriveUploader = googleDriveUploader;
        this.companyDetailsService = companyDetailsService;
    }

    @Transactional
    public InvoiceOutputDto createInvoice(InvoiceInputDto input) {
        Client client = clientRepository.findById(input.getClientId())
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        LocalDate invoiceDate = Optional.ofNullable(input.getInvoiceDate()).orElse(LocalDate.now());

        String invoiceNumber = Optional.ofNullable(input.getInvoiceNumber())
                .filter(n -> !n.isBlank())
                .orElseGet(() -> generateInvoiceNumber(client.getName(), invoiceDate));

        if (invoiceRepository.findByInvoiceNumber(invoiceNumber).isPresent()) {
            throw new IllegalStateException("Duplicate invoice number: " + invoiceNumber);
        }

        Invoice invoice = InvoiceMapper.toEntity(input, client);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceNumber(invoiceNumber);

        validateLines(invoice.getLines());
        fillMissingAmounts(invoice);

        BigDecimal total = invoice.getLines().stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalExclVat(total);
        invoice.setTotalInclVat(total);

        Invoice saved = invoiceRepository.save(invoice);

        // Upload PDF
        try {
            byte[] pdfBytes = generateInvoicePdf(saved.getId());
            MultipartFile pdfFile = new MockMultipartFile(
                    invoiceNumber + ".pdf",
                    invoiceNumber + ".pdf",
                    "application/pdf",
                    pdfBytes
            );
            String driveUrl = googleDriveUploader.uploadToYearFolder(pdfFile, invoiceDate.getYear());

            saved.setDriveUrl(driveUrl);
            invoiceRepository.save(saved);

            System.out.println("Factuur geüpload naar Drive: " + driveUrl);
        } catch (IOException e) {
            throw new RuntimeException("Fout bij uploaden naar Drive", e);
        }

        return InvoiceMapper.toDto(saved);
    }

    public void validateLines(List<InvoiceLine> lines) {
        for (InvoiceLine line : lines) {
            boolean timeBased = line.getDurationMinutes() != null && line.getHourlyRate() != null;
            boolean distanceBased = line.getDistanceKm() != null && line.getRatePerKm() != null;
            boolean hasAmount = line.getAmount() != null;

            int modes = 0;
            if (timeBased) modes++;
            if (distanceBased) modes++;
            if (hasAmount) modes++;

            if (modes == 0) {
                throw new IllegalArgumentException("Invoice line moet uren+tarief, afstand+tarief/km of bedrag bevatten");
            }
            if (modes > 1) {
                throw new IllegalArgumentException("Invoice line mag slechts één type gegevens bevatten (tijd, afstand of vast bedrag)");
            }
        }
    }

    public void fillMissingAmounts(Invoice invoice) {
        for (InvoiceLine line : invoice.getLines()) {
            if (line.getAmount() == null) {
                if (line.getDurationMinutes() != null && line.getHourlyRate() != null) {
                    BigDecimal hours = BigDecimal.valueOf(line.getDurationMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
                    line.setAmount(hours.multiply(line.getHourlyRate()));
                } else if (line.getDistanceKm() != null && line.getRatePerKm() != null) {
                    BigDecimal amount = BigDecimal.valueOf(line.getDistanceKm()).multiply(line.getRatePerKm());
                    line.setAmount(amount);
                } else {
                    throw new IllegalArgumentException("Onvoldoende gegevens om bedrag te berekenen.");
                }
            }
        }
    }

    public InvoiceOutputDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Invoice not found"));
        return InvoiceMapper.toDto(invoice);
    }

    public List<InvoiceOutputDto> getAllInvoices(Integer year) {
        if (year == null) {
            return invoiceRepository.findAll().stream()
                    .map(InvoiceMapper::toDto)
                    .toList();
        }
        return getAllInvoicesByYear(year);
    }

    public List<InvoiceOutputDto> getAllInvoicesByYear(Integer year) {
        return invoiceRepository.findAll().stream()
                .filter(invoice -> invoice.getInvoiceDate().getYear() == year)
                .map(InvoiceMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));

        try {
            googleDriveUploader.deleteFileById(invoice.getDriveUrl());
        } catch (IOException e) {
            throw new RuntimeException("Fout bij het verwijderen van het bestand van Drive", e);
        }

        invoiceRepository.delete(invoice);
    }

    public byte[] generateInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));

        CompanyDetails companyDetails = companyDetailsService.getDetails();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 12);
            Font small = new Font(Font.HELVETICA, 10);

            // Afzendergegevens
            document.add(new Paragraph(companyDetails.getName(), bold));
            document.add(new Paragraph(
                    companyDetails.getStreet() + "\n" +
                            companyDetails.getPostalCode() + ", " + companyDetails.getCity() + "\n" +
                            companyDetails.getPhone() + "\n" +
                            companyDetails.getEmail() + "\n", normal));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph(
                    "IBAN: " + companyDetails.getIban() + "\n" +
                            "BTW-ID: " + companyDetails.getVatNumber() + "\n" +
                            "KvK: " + companyDetails.getChamberOfCommerce() + "\n", small));
            document.add(Chunk.NEWLINE);

            // Klantgegevens
            Client client = invoice.getClient();
            document.add(new Paragraph(client.getName() + " t.a.v. " + client.getContactPerson(), bold));
            document.add(new Paragraph(client.getStreet()));
            document.add(new Paragraph(client.getPostalCode() + ", " + client.getCity()));
            document.add(Chunk.NEWLINE);

            // Factuurdatum en nummer
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", new Locale("nl"));
            document.add(new Paragraph("Factuurdatum: " + invoice.getInvoiceDate().format(fmt), normal));
            document.add(new Paragraph("Factuurnummer: " + invoice.getInvoiceNumber(), bold));
            document.add(Chunk.NEWLINE);

            // Bepalen welke kolommen getoond worden
            boolean showHours = invoice.getLines().stream().anyMatch(l -> l.getDurationMinutes() != null);
            boolean showRate = invoice.getLines().stream().anyMatch(l -> l.getHourlyRate() != null);

            List<String> headers = new ArrayList<>();
            headers.add("Datum");
            headers.add("Omschrijving");
            if (showHours) headers.add("Aantal uren");
            if (showRate) headers.add("Tarief");
            headers.add("Subtotaal");

            int columnCount = headers.size();
            float[] columnWidths = new float[columnCount];
            int idx = 0;
            columnWidths[idx++] = 1.5f;
            columnWidths[idx++] = 4f;
            if (showHours) columnWidths[idx++] = 2f;
            if (showRate) columnWidths[idx++] = 2f;
            columnWidths[idx] = 1.5f;

            PdfPTable table = new PdfPTable(columnCount);
            table.setWidthPercentage(100);
            table.setWidths(columnWidths);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            // Header toevoegen
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, bold));
                cell.setBackgroundColor(new Color(215, 215, 255));
                cell.setBorderColor(new Color(215, 215, 255));
                table.addCell(cell);
            }

            BigDecimal total = BigDecimal.ZERO;
            int totalMinutes = 0;

            boolean shade = false;

            for (InvoiceLine line : invoice.getLines()) {
                LocalDate date = line.getDate();
                Integer minutes = line.getDurationMinutes();
                BigDecimal rate = line.getHourlyRate();
                BigDecimal amount = line.getAmount();
                Integer km = line.getDistanceKm();
                BigDecimal kmRate = line.getRatePerKm();

                total = total.add(amount);
                if (minutes != null) totalMinutes += minutes;

                Color rowColor = shade ? new Color(240, 240, 255) : Color.WHITE;
                shade = !shade;

                // Datum
                PdfPCell dateCell = new PdfPCell(new Phrase(date != null ? date.format(DateTimeFormatter.ofPattern("d/M")) : "-"));
                dateCell.setBackgroundColor(rowColor);
                dateCell.setBorderColor(new Color(200, 200, 255));
                table.addCell(dateCell);

                // Omschrijving
                String description = line.getDescription() != null ? line.getDescription() : "";
                if (km != null && kmRate != null) {
                    description = "Reiskosten " + km + " km à " + formatMoney(kmRate);
                }
                PdfPCell descCell = new PdfPCell(new Phrase(description.isEmpty() ? "-" : description));
                descCell.setBackgroundColor(rowColor);
                descCell.setBorderColor(new Color(200, 200, 255));
                table.addCell(descCell);

                // Uren
                if (showHours) {
                    PdfPCell hoursCell = new PdfPCell(new Phrase(minutes != null ? formatDuration(minutes) : "-"));
                    hoursCell.setBackgroundColor(rowColor);
                    hoursCell.setBorderColor(new Color(200, 200, 255));
                    table.addCell(hoursCell);
                }

                // Tarief
                if (showRate) {
                    PdfPCell rateCell = new PdfPCell(new Phrase(rate != null ? formatMoney(rate) : "-"));
                    rateCell.setBackgroundColor(rowColor);
                    rateCell.setBorderColor(new Color(200, 200, 255));
                    table.addCell(rateCell);
                }

                // Subtotaal
                PdfPCell amountCell = new PdfPCell(new Phrase(formatMoney(amount)));
                amountCell.setBackgroundColor(rowColor);
                amountCell.setBorderColor(new Color(200, 200, 255));
                table.addCell(amountCell);
            }

            // Lege rij
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(columnCount);
            emptyCell.setBorder(Rectangle.NO_BORDER);
            emptyCell.setFixedHeight(20f);
            table.addCell(emptyCell);

            // Totalen
            int totalKm = invoice.getLines().stream()
                    .filter(l -> l.getDistanceKm() != null)
                    .mapToInt(InvoiceLine::getDistanceKm)
                    .sum();

            String totalLabel = showHours ? "Totaal " + formatDuration(totalMinutes) :
                    totalKm > 0 ? "Totaal " + totalKm + " km" :
                            "Totaal";

            Color totalRowColor = new Color(240, 240, 255);
            Color finalRowColor = new Color(225, 225, 255);

            table.addCell(makeRightAlignedCell(totalLabel, columnCount - 1, bold, null));
            table.addCell(makeRightAlignedCell(formatMoney(total), 1, bold, null));

            table.addCell(makeRightAlignedCell("Totaal exclusief BTW", columnCount - 1, normal, totalRowColor));
            table.addCell(makeRightAlignedCell(formatMoney(invoice.getTotalExclVat()), 1, normal, totalRowColor));

            table.addCell(makeRightAlignedCell("Vrijstelling OB o.g.v. art. 25 Wet OB", columnCount - 1, normal, null));
            table.addCell(makeRightAlignedCell("€ 0,00", 1, normal, null));

            table.addCell(makeRightAlignedCell("Totaal inclusief BTW", columnCount - 1, bold, finalRowColor));
            table.addCell(makeRightAlignedCell(formatMoney(invoice.getTotalInclVat()), 1, bold, finalRowColor));

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Afsluiting
            document.add(new Paragraph("Gelieve het bovenstaande bedrag binnen 21 dagen over te maken onder vermelding van factuurnummer en naam.", normal));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Hartelijk dank voor de medewerking.", normal));
            document.add(new Paragraph("Met vriendelijke groet,", normal));
            document.add(new Paragraph(companyDetails.getName(), normal));

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        } finally {
            document.close();
        }

        return out.toByteArray();
    }

    private String generateInvoiceNumber(String clientName, LocalDate date) {
        String prefix = clientName.replaceAll("\\s+", "").substring(0, 2).toUpperCase();
        return prefix + date.getYear() + String.format("%02d", date.getMonthValue());
    }
}
