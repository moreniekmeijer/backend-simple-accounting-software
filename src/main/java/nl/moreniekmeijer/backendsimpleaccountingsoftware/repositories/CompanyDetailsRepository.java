package nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.CompanyDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {
    Optional<CompanyDetails> findFirstBy();
}
