package nl.moreniekmeijer.backendsimpleaccountingsoftware.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String driveUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "invoice_id")
    private List<InvoiceLine> lines = new ArrayList<>();

    private BigDecimal totalExclVat;
    private BigDecimal totalInclVat;

    private boolean vatExempt = true; // altijd vrijgesteld o.g.v. artikel 25

    public void setLines(List<InvoiceLine> lines) {
        this.lines = new ArrayList<>(lines);
    }
}