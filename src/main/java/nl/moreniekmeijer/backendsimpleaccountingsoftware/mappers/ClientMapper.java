package nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public static Client toEntity(ClientInputDto dto) {
        return new Client(null, dto.getName(), dto.getContactPerson(), dto.getStreet(), dto.getPostalCode(), dto.getCity());
    }

    public static ClientOutputDto toDto(Client client) {
        return new ClientOutputDto(client.getId(), client.getName(), client.getContactPerson(),
                client.getStreet(), client.getPostalCode(), client.getCity());
    }

    public static void updateEntity(Client client, ClientInputDto dto) {
        client.setName(dto.getName());
        client.setContactPerson(dto.getContactPerson());
        client.setStreet(dto.getStreet());
        client.setPostalCode(dto.getPostalCode());
        client.setCity(dto.getCity());
    }
}
