package nl.moreniekmeijer.backendsimpleaccountingsoftware.services;

import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.mappers.ClientMapper;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.models.Client;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<ClientOutputDto> getAllClients() {
        return clientRepository.findAll().stream().map(ClientMapper::toDto).toList();
    }

    public ClientOutputDto createClient(ClientInputDto dto) {
        Client saved = clientRepository.save(ClientMapper.toEntity(dto));
        return ClientMapper.toDto(saved);
    }

    public ClientOutputDto updateClient(Long id, ClientInputDto dto) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Client not found"));
        ClientMapper.updateEntity(client, dto);
        return ClientMapper.toDto(clientRepository.save(client));
    }

    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) throw new NoSuchElementException("Client not found");
        clientRepository.deleteById(id);
    }
}
