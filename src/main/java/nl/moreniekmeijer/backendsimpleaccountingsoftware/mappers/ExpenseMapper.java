package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

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

    public static Expense fromParsedDto(ParsedReceiptDto parsedDto, MultipartFile file) throws IOException {
        Expense expense = new Expense();
        expense.setDate(parsedDto.getDate());
        expense.setInvoiceNumber(parsedDto.getInvoiceNumber());
        expense.setAmount(parsedDto.getAmount());
        expense.setVat(parsedDto.getVat());
        expense.setVendor(parsedDto.getVendor());

        if (file != null && !file.isEmpty()) {
            expense.setReceipt(file.getBytes());
            expense.setFileType(file.getContentType());
        }

        return expense;
    }

    public static void updateExpenseFields(Expense target, Expense source) {
        target.setDate(source.getDate());
        target.setInvoiceNumber(source.getInvoiceNumber());
        target.setAmount(source.getAmount());
        target.setVat(source.getVat());
        target.setVendor(source.getVendor());
        target.setCategory(source.getCategory());
    }
}
