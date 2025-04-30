package controllers;

import dtos.InvoiceInputDto;
import dtos.InvoiceOutputDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.InvoiceService;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService service;

    public InvoiceController(InvoiceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<InvoiceOutputDto> create(@RequestBody @Valid InvoiceInputDto input) {
        InvoiceOutputDto output = service.createInvoice(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceOutputDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getInvoice(id));
    }
}
