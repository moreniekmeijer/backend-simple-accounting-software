package models;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Client {
    private String name;
    private String contactPerson;
    private String street;
    private String postalCode;
    private String city;
}
