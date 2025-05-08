package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.InvestmentOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ParsedReceiptDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;

public class ExpenseMapper {

    public static ExpenseOutputDto toResponseDto(Expense expense) {
        InvestmentOutputDto investmentDto = null;
        if (expense.getInvestmentDetails() != null) {
            investmentDto = InvestmentMapper.toDto(expense.getInvestmentDetails());
        }

        ExpenseOutputDto dto = new ExpenseOutputDto();
        dto.setId(expense.getId());
        dto.setDate(expense.getDate());
        dto.setInvoiceNumber(expense.getInvoiceNumber());
        dto.setAmount(expense.getAmount());
        dto.setVendor(expense.getVendor());
        dto.setCategory(expense.getCategory());
        dto.setVat(expense.getVat());
        dto.setDriveUrl(expense.getDriveUrl());
        dto.setInvestmentDetails(investmentDto);

        return dto;
    }

    public static ExpenseDto fromParsedDto(ParsedReceiptDto parsedDto) {
        ExpenseDto dto = new ExpenseDto();
        dto.setDate(parsedDto.getDate());
        dto.setInvoiceNumber(parsedDto.getInvoiceNumber());
        dto.setAmount(parsedDto.getAmount());
        dto.setVat(parsedDto.getVat());
        dto.setVendor(parsedDto.getVendor());
        return dto;
    }

    public static void updateExpenseFields(Expense target, Expense source) {
        target.setDate(source.getDate());
        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setAmount(source.getAmount());
        target.setVat(source.getVat());
        target.setVendor(source.getVendor());
        target.setCategory(source.getCategory());
    }

    public static Expense fromDto(ExpenseDto dto) {
        Expense expense = new Expense();
        expense.setDate(dto.getDate());
        expense.setInvoiceNumber(dto.getInvoiceNumber());
        expense.setAmount(dto.getAmount());
        expense.setVat(dto.getVat());
        expense.setVendor(dto.getVendor());
        expense.setCategory(dto.getCategory());
        return expense;
    }
}