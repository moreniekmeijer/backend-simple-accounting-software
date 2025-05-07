package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceLineDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvoiceOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Invoice;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvoiceLine;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InvoiceMapper {

    public static Invoice toEntity(InvoiceInputDto dto, Client client) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(dto.getInvoiceNumber());
        invoice.setInvoiceDate(dto.getInvoiceDate());
        invoice.setClient(client);

        invoice.setLines(dto.getLines().stream()
                .map(InvoiceMapper::toEntity)
                .toList());

        BigDecimal total = invoice.getLines().stream()
                .map(InvoiceLine::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        invoice.setTotalExclVat(total);
        invoice.setTotalInclVat(total);
        return invoice;
    }

    public static InvoiceOutputDto toDto(Invoice invoice) {
        InvoiceOutputDto dto = new InvoiceOutputDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setInvoiceDate(invoice.getInvoiceDate());
        dto.setClient(ClientMapper.toDto(invoice.getClient()));
        dto.setLines(invoice.getLines().stream()
                .map(InvoiceMapper::toDto)
                .toList());
        dto.setTotalExclVat(invoice.getTotalExclVat());
        dto.setTotalInclVat(invoice.getTotalInclVat());
        dto.setDriveUrl(invoice.getDriveUrl());
        return dto;
    }

    private static InvoiceLine toEntity(InvoiceLineDto dto) {
        InvoiceLine line = new InvoiceLine();
        line.setDescription(dto.getDescription());
        line.setDate(dto.getDate());
        line.setDurationMinutes(dto.getDurationMinutes());
        line.setHourlyRate(dto.getHourlyRate());
        line.setAmount(dto.getAmount());
        return line;
    }

    private static InvoiceLineDto toDto(InvoiceLine line) {
        InvoiceLineDto dto = new InvoiceLineDto();
        dto.setDescription(line.getDescription());
        dto.setDate(line.getDate());
        dto.setDurationMinutes(line.getDurationMinutes());
        dto.setHourlyRate(line.getHourlyRate());
        dto.setAmount(line.getAmount());
        return dto;
    }
}
