package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;

import jakarta.validation.Valid;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.CompanyDetailsDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.CompanyDetails;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.services.CompanyDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
public class CompanyDetailsController {

    private final CompanyDetailsService companyDetailsService;

    public CompanyDetailsController(CompanyDetailsService companyDetailsService) {
        this.companyDetailsService = companyDetailsService;
    }

    @GetMapping
    public ResponseEntity<CompanyDetails> getDetails() {
        return ResponseEntity.ok(companyDetailsService.getDetails());
    }

    @PutMapping
    public ResponseEntity<CompanyDetails> updateCompanyDetails(@Valid @RequestBody CompanyDetailsDto dto) {
        companyDetailsService.updateCompanyDetails(dto);
        return ResponseEntity.ok(companyDetailsService.getDetails());
    }
}
