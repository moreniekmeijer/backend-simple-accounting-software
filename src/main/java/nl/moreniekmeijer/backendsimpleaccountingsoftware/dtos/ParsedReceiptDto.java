package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedReceiptDto {
    private String vendor;
    private String invoiceNumber;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal vat;
}
