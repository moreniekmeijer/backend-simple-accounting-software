package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private ClientDto client;
    private List<InvoiceLineDto> lines;
    private BigDecimal totalExclVat;
    private BigDecimal totalInclVat;
}
