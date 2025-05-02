package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientInputDto {

    @NotNull
    private String name;

    private String contactPerson;
    private String street;
    private String postalCode;
    private String city;
}
