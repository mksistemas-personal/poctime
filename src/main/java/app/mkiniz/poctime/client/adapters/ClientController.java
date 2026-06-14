package app.mkiniz.poctime.client.adapters;

import app.mkiniz.poctime.client.domain.*;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/client")
@AllArgsConstructor
@Validated
public class ClientController {

    private final AddBusinessUseCase<ClientRequest, ClientResponse> addClientService;
    private final UpdateBusinessUseCase<Tsid, UpdateClientRequest, ClientResponse> updateClientService;
    private final DeleteBusinessUseCase<Tsid, ClientResponse> deleteClientService;
    private final GetByIdBusinessUseCase<Tsid, ClientResponse> getClientByIdService;
    private final GetAllBusinessUseCase<ClientSearchRequest, Maybe<Slice<ClientResponse>>> getAllClientService;
    private final GetAllBusinessUseCase<String, Maybe<Slice<ClientProjectionResponse>>> getAllClientProjectionService;


    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addClientService.execute(request));
    }

    @GetMapping
    public ResponseEntity<Slice<ClientResponse>> getAllClient(Pageable pageable,
                                                              @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) String street,
                                                              @RequestParam(required = false) String city,
                                                              @RequestParam(required = false) String stateCode,
                                                              @RequestParam(required = false) String email) {
        ClientSearchRequest searchRequest = new ClientSearchRequest(name, street, city, stateCode, email);
        return getAllClientService.execute(pageable, searchRequest)
                .map(clients -> ResponseEntity.status(HttpStatus.OK).body(clients))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Tsid id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getClientByIdService.execute(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Tsid id, @Valid @RequestBody UpdateClientRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateClientService.execute(id, request));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<ClientResponse> deleteClient(@PathVariable Tsid id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deleteClientService.execute(id));
    }

    @GetMapping(path = "/projection/all-with-city")
    public ResponseEntity<Slice<ClientProjectionResponse>> getAllClientsProjection(Pageable pageable) {
        return getAllClientProjectionService.execute(pageable, null)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }
}
