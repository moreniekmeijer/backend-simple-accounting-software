package nl.moreniekmeijer.backendsimpleaccountingsoftware.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Expense expense;

    private LocalDate purchaseDate;
    private BigDecimal purchaseAmount;
    private Integer depreciationYears;
    private BigDecimal residualValue;

    public BigDecimal getAnnualDepreciation() {
        return (purchaseAmount.subtract(residualValue))
                .divide(BigDecimal.valueOf(depreciationYears), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getBookValue(LocalDate onDate) {
        int yearsElapsed = Period.between(purchaseDate, onDate).getYears();
        BigDecimal depreciated = getAnnualDepreciation().multiply(BigDecimal.valueOf(Math.min(yearsElapsed, depreciationYears)));
        return purchaseAmount.subtract(depreciated).max(residualValue);
    }
}
