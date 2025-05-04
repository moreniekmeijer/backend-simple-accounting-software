package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.InvoiceMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Invoice;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvoiceLine;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ClientRepository;
import org.springframework.stereotype.Service;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.InvoiceRepository;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

import static nl.moreniekmeijer.backendsimpleaccountingsoftware.utils.InvoiceUtils.*;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, ClientRepository clientRepository) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
    }

    public InvoiceOutputDto createInvoice(InvoiceInputDto input) {
        Client client = clientRepository.findById(input.getClientId())
                .orElseThrow(() -> new NoSuchElementException("Client not found"));

        LocalDate invoiceDate = input.getInvoiceDate() != null
                ? input.getInvoiceDate()
                : LocalDate.now();

        String invoiceNumber = input.getInvoiceNumber();
        if (invoiceNumber == null || invoiceNumber.isBlank()) {
            invoiceNumber = generateInvoiceNumber(client.getName(), invoiceDate);
        }

        if (invoiceRepository.findByInvoiceNumber(invoiceNumber).isPresent()) {
            throw new IllegalStateException("Duplicate invoice number: " + invoiceNumber);
        }

        Invoice invoice = InvoiceMapper.toEntity(input, client);
        invoice.setInvoiceDate(invoiceDate);
        invoice.setInvoiceNumber(invoiceNumber);

        Invoice saved = invoiceRepository.save(invoice);
        return InvoiceMapper.toDto(saved);
    }

    public InvoiceOutputDto getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Invoice not found"));
        return InvoiceMapper.toDto(invoice);
    }

    public List<InvoiceOutputDto> getAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(InvoiceMapper::toDto)
                .toList();
    }

    public byte[] generateInvoicePdf(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Invoice not found"));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 50, 50, 50, 50);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font bold = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normal = new Font(Font.HELVETICA, 12);
            Font small = new Font(Font.HELVETICA, 10);

            // Afzendergegevens
            document.add(new Paragraph("Boekhouding Niek Meijer 2025", bold));
            document.add(new Paragraph("Muntkade 130\n3531 AK, Utrecht\n0316 30273214\nniekjmeijer@gmail.com\n", normal));
            document.add(new Paragraph("IBAN: NL34 INGB 0008 3564 43\nBTW-ID: NL002312181B48\nKvK: 73978914\n", small));
            document.add(Chunk.NEWLINE);

            // Klantgegevens
            Client client = invoice.getClient();
            document.add(new Paragraph(client.getName() + " t.a.v. " + client.getContactPerson(), bold));
            document.add(new Paragraph(client.getStreet()));
            document.add(new Paragraph(client.getPostalCode() + ", " + client.getCity()));
            document.add(Chunk.NEWLINE);

            // Factuurdatum + nummer
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", new Locale("nl"));
            document.add(new Paragraph("Factuurdatum: " + invoice.getInvoiceDate().format(fmt), normal));
            document.add(new Paragraph("Factuurnummer: " + invoice.getInvoiceNumber(), bold));
            document.add(Chunk.NEWLINE);

            // Tabel kop
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2});
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            table.addCell(new Phrase("Dag (dd/mm)", bold));
            table.addCell(new Phrase("Aantal uren", bold));
            table.addCell(new Phrase("Tarief", bold));
            table.addCell(new Phrase("Subtotaal", bold));

            BigDecimal total = BigDecimal.ZERO;
            int totalMinutes = 0;

            for (InvoiceLine line : invoice.getLines()) {
                LocalDate date = line.getDate();
                Integer minutes = line.getDurationMinutes();
                BigDecimal rate = line.getHourlyRate();
                BigDecimal amount = line.getAmount();

                total = total.add(amount);
                if (minutes != null) totalMinutes += minutes;

                table.addCell(date != null ? date.format(DateTimeFormatter.ofPattern("d/M")) : "-");
                table.addCell(minutes != null ? formatDuration(minutes) : "-");
                table.addCell(rate != null ? formatMoney(rate) : "-");
                table.addCell(formatMoney(amount));
            }

            // Lege rij
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(4);
            emptyCell.setBorder(Rectangle.NO_BORDER);
            table.addCell(emptyCell);

            // Totalen
            table.addCell(makeRightAlignedCell("Totaal " + formatDuration(totalMinutes), 3, bold));
            table.addCell(makeRightAlignedCell(formatMoney(total), 1, bold));

            table.addCell(makeRightAlignedCell("Totaal exclusief BTW", 3, normal));
            table.addCell(makeRightAlignedCell(formatMoney(invoice.getTotalExclVat()), 1, normal));

            table.addCell(makeRightAlignedCell("Vrijstelling OB o.g.v. art. 25 Wet OB", 3, normal));
            table.addCell(makeRightAlignedCell("€ 0,00", 1, normal));

            table.addCell(makeRightAlignedCell("Totaal inclusief BTW", 3, bold));
            table.addCell(makeRightAlignedCell(formatMoney(invoice.getTotalInclVat()), 1, bold));

            document.add(table);
            document.add(Chunk.NEWLINE);

            // Betaalinstructie en afsluiter
            document.add(new Paragraph("Gelieve het bovenstaande bedrag binnen 21 dagen over te maken onder vermelding van factuurnummer en naam.", normal));
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Hartelijk dank voor de medewerking.", normal));
            document.add(new Paragraph("Met vriendelijke groet,", normal));
            document.add(new Paragraph("Niek Meijer", normal));

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
