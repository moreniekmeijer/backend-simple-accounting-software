package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.InvoiceMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Invoice;
import org.springframework.stereotype.Service;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.InvoiceRepository;

import java.util.NoSuchElementException;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceMapper invoiceMapper) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceMapper = invoiceMapper;
    }

    public InvoiceOutputDto createInvoice(InvoiceInputDto input) {
        try {
            Invoice invoice = invoiceMapper.toEntity(input);
            Invoice saved = invoiceRepository.save(invoice);
            return invoiceMapper.toDto(saved);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save invoice", e);
        }
    }

    public InvoiceOutputDto getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Invoice not found"));
        return invoiceMapper.toDto(invoice);
    }
}
