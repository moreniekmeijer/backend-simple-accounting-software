package mappers;

import dtos.ClientDto;
import dtos.InvoiceInputDto;
import dtos.InvoiceLineDto;
import dtos.InvoiceOutputDto;
import models.Client;
import models.Invoice;
import models.InvoiceLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvoiceMapper {

    public Invoice toEntity(InvoiceInputDto dto) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setClient(toEntity(dto.getClient()));
        invoice.setLines(dto.getLines().stream()
                .map(this::toEntity)
                .toList());

        BigDecimal total = invoice.getLines().stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalExclVat(total);
        invoice.setTotalInclVat(total); // VAT always 0 due to exemption
        return invoice;
    }

    public InvoiceOutputDto toDto(Invoice invoice) {
        InvoiceOutputDto dto = new InvoiceOutputDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setClient(toDto(invoice.getClient()));
        dto.setLines(invoice.getLines().stream()
                .map(this::toDto)
                .toList());
        dto.setTotalExclVat(invoice.getTotalExclVat());
        dto.setTotalInclVat(invoice.getTotalInclVat());
        return dto;
    }

    private Client toEntity(ClientDto dto) {
        Client c = new Client();
        c.setName(dto.getName());
        c.setContactPerson(dto.getContactPerson());
        c.setStreet(dto.getStreet());
        c.setPostalCode(dto.getPostalCode());
        c.setCity(dto.getCity());
        return c;
    }

    private ClientDto toDto(Client c) {
        ClientDto dto = new ClientDto();
        dto.setName(c.getName());
        dto.setContactPerson(c.getContactPerson());
        dto.setStreet(c.getStreet());
        dto.setPostalCode(c.getPostalCode());
        dto.setCity(c.getCity());
        return dto;
    }

    private InvoiceLine toEntity(InvoiceLineDto dto) {
        InvoiceLine line = new InvoiceLine();
        line.setDescription(dto.getDescription());
        line.setDate(dto.getDate());
        line.setDurationMinutes(dto.getDurationMinutes());
        line.setHourlyRate(dto.getHourlyRate());
        line.setAmount(dto.getAmount());
        return line;
    }

    private InvoiceLineDto toDto(InvoiceLine line) {
        InvoiceLineDto dto = new InvoiceLineDto();
        dto.setDescription(line.getDescription());
        dto.setDate(line.getDate());
        dto.setDurationMinutes(line.getDurationMinutes());
        dto.setHourlyRate(line.getHourlyRate());
        dto.setAmount(line.getAmount());
        return dto;
    }
}
