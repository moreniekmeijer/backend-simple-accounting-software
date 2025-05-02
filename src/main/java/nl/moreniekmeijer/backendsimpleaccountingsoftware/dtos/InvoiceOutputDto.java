package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceOutputDto {
    private Long id;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private ClientOutputDto client;
    private List<InvoiceLineDto> lines;
    private BigDecimal totalExclVat;
    private BigDecimal totalInclVat;
}
