package nl.moreniekmeijer.backendsimpleaccountingsoftware.controllers;

import jakarta.validation.Valid;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientInputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.dtos.ClientOutputDto;
import nl.moreniekmeijer.backendsimpleaccountingsoftware.services.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService service) {
        this.clientService = service;
    }

    @GetMapping
    public List<ClientOutputDto> getAll() {
        return clientService.getAllClients();
    }

    @PostMapping
    public ResponseEntity<ClientOutputDto> create(@RequestBody @Valid ClientInputDto input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(input));
    }

    @PutMapping("/{id}")
    public ClientOutputDto update(@PathVariable Long id, @RequestBody @Valid ClientInputDto input) {
        return clientService.updateClient(id, input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
