package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ParsedReceiptDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class ExpenseMapper {

    private static final String RECEIPT_ENDPOINT_TEMPLATE = "/expenses/%d/receipt";

    public static ExpenseOutputDto toResponseDto(Expense expense) {
        if (expense == null) return null;

        ExpenseOutputDto dto = new ExpenseOutputDto();
        dto.setId(expense.getId());
        dto.setDate(expense.getDate());
        dto.setInvoiceNumber(expense.getInvoiceNumber());
        dto.setAmount(expense.getAmount());
        dto.setVendor(expense.getVendor());
        dto.setCategory(expense.getCategory());
        dto.setVat(expense.getVat());
        dto.setFileType(expense.getFileType());

        if (expense.getReceipt() != null) {
            dto.setReceiptUrl(String.format(RECEIPT_ENDPOINT_TEMPLATE, expense.getId()));
        }

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

    public static Expense fromDto(ExpenseDto dto, MultipartFile file) {
        Expense expense = fromDto(dto);
        if (file != null && !file.isEmpty()) {
            try {
                expense.setReceipt(file.getBytes());
                expense.setFileType(file.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Fout bij lezen bestand", e);
            }
        }
        return expense;
    }
}