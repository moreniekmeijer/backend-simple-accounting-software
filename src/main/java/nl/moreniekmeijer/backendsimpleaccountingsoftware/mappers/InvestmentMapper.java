package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvestmentDetails;

import java.time.LocalDate;

public class InvestmentMapper {

    public static InvestmentDetails toEntity(Expense expense, InvestmentInputDto dto) {
        InvestmentDetails details = new InvestmentDetails();
        details.setExpense(expense);
        details.setPurchaseDate(expense.getDate());
        details.setPurchaseAmount(expense.getAmount());
        details.setDepreciationYears(dto.getDepreciationYears());
        details.setResidualValue(dto.getResidualValue());
        return details;
    }

    public static InvestmentOutputDto toDto(InvestmentDetails entity) {
        if (entity == null) return null;

        InvestmentOutputDto dto = new InvestmentOutputDto();
        dto.setId(entity.getId());
        dto.setDepreciationYears(entity.getDepreciationYears());
        dto.setResidualValue(entity.getResidualValue());
        dto.setAnnualDepreciation(entity.getAnnualDepreciation());
        dto.setBookValue(entity.getBookValue(LocalDate.now()));

        return dto;
    }
}
