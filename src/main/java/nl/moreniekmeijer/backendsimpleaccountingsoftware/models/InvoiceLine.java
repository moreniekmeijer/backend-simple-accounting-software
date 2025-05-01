package nl.moreniekmeijer.backendsimpleaccountingsoftware.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class InvoiceLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    // Optioneel: datum van uitvoering (bijv. "22-03-2025")
    private LocalDate date;

    // Optioneel: duur in minuten
    private Integer durationMinutes;

    // Optioneel: tarief per uur
    private BigDecimal hourlyRate;

    // Verplicht: subtotaalbedrag
    private BigDecimal amount;
}
