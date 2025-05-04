package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceOutputDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.services.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceOutputDto> createInvoice(@RequestBody @Valid InvoiceInputDto input) {
        InvoiceOutputDto output = invoiceService.createInvoice(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceOutputDto> getInvoiceById(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceOutputDto>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long id) {
        byte[] pdfBytes = invoiceService.generateInvoicePdf(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .body(pdfBytes);
    }

}
