package nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.InvestmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestmentRepository extends JpaRepository<InvestmentDetails, Long> {
}
