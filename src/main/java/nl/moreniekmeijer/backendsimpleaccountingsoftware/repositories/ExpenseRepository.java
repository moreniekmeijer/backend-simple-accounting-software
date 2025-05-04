package nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
