package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class InvoiceOutputDto {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private ClientOutputDto client;
    private List<InvoiceLineDto> lines;
    private BigDecimal totalExclVat;
    private BigDecimal totalInclVat;
    private String driveUrl;
}
