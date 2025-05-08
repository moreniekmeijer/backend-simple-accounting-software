package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvestmentOutputDto {
    private Long id;
    private Integer depreciationYears;
    private BigDecimal residualValue;
    private BigDecimal annualDepreciation;
    private BigDecimal bookValue;
}


