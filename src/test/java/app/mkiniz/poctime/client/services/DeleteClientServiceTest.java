package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.DeleteBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private TsidGenerator generator;

    @InjectMocks
    private DeleteClientService deleteClientService;

    private DeleteBaseBusinessTest<Tsid, ClientResponse> baseTest;
    private Tsid clientId;
    private Client client;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        this.baseTest = DeleteBaseBusinessTest.of();
        Tsid personId = generator.newTsid();
        Address address = Address.builder()
                .state("Santa Catarina")
                .city("Lages")
                .stateCode("SC")
                .street("Avenida Belisario Ramos")
                .zipCode("88596-000")
                .complement("Casa")
                .neighborhood("Guadalupe")
                .number("3185")
                .country("BR")
                .build();
        String clientEmail = "client@example.com";
        Person person = Person.builder()
                .id(personId.toLong())
                .name("John Doe")
                .document(new CnpjDocument("45335153000134"))
                .build();
        this.client = Client.builder()
                .id(personId.toLong())
                .deleted(false)
                .address(address)
                .clientEmail(clientEmail)
                .person(person)
                .build();
        clientId = Tsid.from(client.getId());
        this.clientResponse = ClientResponse.from(client);
    }

    @Test
    void deleteClientPerfectTest() {
        this.baseTest
                .given(() -> {
                    when(clientRepository.findById(clientId.toLong())).thenReturn(Optional.of(client));
                    doNothing().when(clientRepository).delete(client);
                    return clientId;
                })
                .when((id) -> deleteClientService.execute(id))
                .then((id, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response.clientId()).isEqualTo(clientResponse.clientId());
                    verify(clientRepository, times(1)).delete(client);
                    verify(clientRepository, times(1)).findById(clientId.toLong());
                })
                .execute();
    }
}
