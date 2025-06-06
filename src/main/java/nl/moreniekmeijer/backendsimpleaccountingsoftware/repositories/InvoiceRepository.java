package nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
