package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDto {
    private String name;
    private String contactPerson;
    private String street;
    private String postalCode;
    private String city;
}
