package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ExpenseOutputDto;
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

    @PostMapping(value = "/parse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpenseDto> parseReceipt(@RequestParam("file") MultipartFile file) {
        try {
            ExpenseDto parsedDto = expenseService.parseReceiptToDto(file);
            return ResponseEntity.ok(parsedDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExpenseOutputDto> saveExpense(
            @RequestPart("expense") ExpenseDto dto,
            @RequestPart("file") MultipartFile file
    ) {
        ExpenseOutputDto saved = expenseService.saveExpense(dto, file);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseOutputDto> updateExpense(@PathVariable Long id, @RequestBody ExpenseDto dto) {
        return expenseService.updateExpense(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseOutputDto> getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseOutputById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ExpenseOutputDto>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenseOutputs());
    }

    @GetMapping("/{id}/receipt")
    public ResponseEntity<byte[]> getReceipt(@PathVariable Long id) {
        return expenseService.getReceiptFile(id)
                .map(data -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(data.fileType()))
                        .body(data.file()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}