package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ParsedReceiptDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.ExpenseMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.services.ExpenseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpenseOutputDto> uploadExpense(@RequestParam("file") MultipartFile file) {
        try {
            ParsedReceiptDto parsedReceipt = expenseService.parseReceipt(file);
            Expense savedExpense = expenseService.saveParsedReceipt(parsedReceipt, file);
            return ResponseEntity.ok(ExpenseMapper.toResponseDto(savedExpense));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseOutputDto> updateExpense(@PathVariable Long id, @RequestBody Expense updatedExpense) {
        return expenseService.updateExpense(id, updatedExpense)
                .map(ExpenseMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseOutputDto> getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .map(ExpenseMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExpenseOutputDto>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        List<ExpenseOutputDto> dtos = expenses.stream()
                .map(ExpenseMapper::toResponseDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .filter(expense -> expense.getReceipt() != null)
                .map(expense -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(expense.getFileType()))
                        .body(expense.getReceipt()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
