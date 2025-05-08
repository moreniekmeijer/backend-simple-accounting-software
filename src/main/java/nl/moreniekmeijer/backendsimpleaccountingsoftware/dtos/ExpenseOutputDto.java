package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ExpenseOutputDto {
    private Long id;
    private LocalDate date;
    private String invoiceNumber;
    private BigDecimal amount;
    private String vendor;
    private String category;
    private BigDecimal vat;
    private String driveUrl;

    private InvestmentOutputDto investmentDetails;
}
