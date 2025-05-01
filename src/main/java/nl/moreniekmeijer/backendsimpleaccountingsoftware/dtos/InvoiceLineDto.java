package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLineDto {
    private String description;
    private LocalDate date; // optional
    private Integer durationMinutes; // optional
    private BigDecimal hourlyRate; // optional
    private BigDecimal amount; // required
}
