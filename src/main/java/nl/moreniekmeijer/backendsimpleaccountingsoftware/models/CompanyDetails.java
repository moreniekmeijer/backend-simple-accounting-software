package nl.moreniekmeijer.backendsimpleaccountingsoftware.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetails {

    @Id
    private Long id = 1L;

    private String name;
    private String street;
    private String postalCode;
    private String city;
    private String phone;
    private String email;
    private String iban;
    private String vatNumber;
    private String chamberOfCommerce;
}
