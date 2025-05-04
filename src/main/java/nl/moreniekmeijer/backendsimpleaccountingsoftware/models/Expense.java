package nl.moreniekmeijer.backendsimpleaccountingsoftware.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String invoiceNumber;
    private BigDecimal amount;
    private String vendor;
    private String category;
    private BigDecimal vat; // bijv. 0.21 voor 21%

    @Lob
    private byte[] receipt; // gescande bon (optioneel)

    private String fileType; // bijv. "application/pdf" of "image/jpeg"
}
