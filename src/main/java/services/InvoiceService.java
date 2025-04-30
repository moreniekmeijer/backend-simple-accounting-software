package services;

import dtos.InvoiceInputDto;
import dtos.InvoiceOutputDto;
import mappers.InvoiceMapper;
import models.Invoice;
import org.springframework.stereotype.Service;
import repositories.InvoiceRepository;

import java.util.NoSuchElementException;

@Service
public class InvoiceService {

    private final InvoiceRepository repo;
    private final InvoiceMapper mapper;

    public InvoiceService(InvoiceRepository repo, InvoiceMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public InvoiceOutputDto createInvoice(InvoiceInputDto input) {
        try {
            Invoice invoice = mapper.toEntity(input);
            Invoice saved = repo.save(invoice);
            return mapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save invoice", e);
        }
    }

    public InvoiceOutputDto getInvoice(Long id) {
        Invoice invoice = repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("Invoice not found"));
        return mapper.toDto(invoice);
    }
}
