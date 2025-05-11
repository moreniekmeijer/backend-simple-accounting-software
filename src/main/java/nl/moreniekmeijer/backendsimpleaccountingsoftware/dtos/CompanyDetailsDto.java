package nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDetailsDto {

    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name should be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Street cannot be null")
    @Size(min = 1, max = 200, message = "Street should be between 1 and 200 characters")
    private String street;

    @NotNull(message = "Postal code cannot be null")
    @Size(min = 5, max = 10, message = "Postal code should be between 5 and 10 characters")
    private String postalCode;

    @NotNull(message = "City cannot be null")
    @Size(min = 1, max = 100, message = "City should be between 1 and 100 characters")
    private String city;

    @NotNull(message = "Phone cannot be null")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be in a valid format")
    private String phone;

    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "IBAN cannot be null")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{1,30}$", message = "IBAN should be valid")
    private String iban;

    @NotNull(message = "VAT number cannot be null")
    @Size(min = 1, max = 15, message = "VAT number should be between 1 and 15 characters")
    private String vatNumber;

    @NotNull(message = "Chamber of Commerce number cannot be null")
    @Size(min = 1, max = 15, message = "Chamber of Commerce number should be between 1 and 15 characters")
    private String chamberOfCommerce;
}
