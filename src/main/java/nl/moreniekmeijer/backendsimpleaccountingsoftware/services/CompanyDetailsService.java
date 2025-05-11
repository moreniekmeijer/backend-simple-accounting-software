package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.CompanyDetailsDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.CompanyDetails;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.CompanyDetailsRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyDetailsService {

    private final CompanyDetailsRepository companyDetailsRepository;

    public CompanyDetailsService(CompanyDetailsRepository companyDetailsRepository) {
        this.companyDetailsRepository = companyDetailsRepository;
    }

    public CompanyDetails getDetails() {
        return companyDetailsRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company details not found"));
    }

    public void updateCompanyDetails(CompanyDetailsDto dto) {
        CompanyDetails existing = companyDetailsRepository.findFirstBy().orElse(new CompanyDetails());

        existing.setName(dto.getName());
        existing.setStreet(dto.getStreet());
        existing.setPostalCode(dto.getPostalCode());
        existing.setCity(dto.getCity());
        existing.setPhone(dto.getPhone());
        existing.setEmail(dto.getEmail());
        existing.setIban(dto.getIban());
        existing.setVatNumber(dto.getVatNumber());
        existing.setChamberOfCommerce(dto.getChamberOfCommerce());

        companyDetailsRepository.save(existing);
    }
}