package nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {}
