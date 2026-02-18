package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.GetAllBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.DefaultQueryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private TsidGenerator generator;

    private GetAllBaseBusinessTest<Specification<Client>, Slice<ClientResponse>> baseTest;

    private Client client1;
    private Client client2;

    @BeforeEach
    void setUp() {
        this.baseTest = GetAllBaseBusinessTest.of();

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

        Person person1 = Person.builder()
                .id(generator.newTsid().toLong())
                .name("John Doe Client")
                .document(new CnpjDocument("45335153000134"))
                .build();

        Person person2 = Person.builder()
                .id(generator.newTsid().toLong())
                .name("Jane Doe Client")
                .document(new CnpjDocument("45335153000135"))
                .build();

        this.client1 = Client.builder()
                .id(generator.newTsid().toLong())
                .deleted(false)
                .address(address)
                .clientEmail("client1@example.com")
                .person(person1)
                .build();

        this.client2 = Client.builder()
                .id(generator.newTsid().toLong())
                .deleted(false)
                .address(address)
                .clientEmail("client2@example.com")
                .person(person2)
                .build();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllPerfectWithSpecificationTest() {
        final Pageable pageable = Pageable.ofSize(10);
        this.baseTest
                .given(() -> {
                    when(clientRepository
                            .findAll(any(Specification.class), any(Pageable.class)))
                            .thenReturn(
                                    new PageImpl<>(List.of(client1, client2)));
                    return new Like<>(new DefaultQueryContext(), "clientEmail", "client");
                })
                .when((pageableData, request) -> {
                    GetAllClientService service = new GetAllClientService(clientRepository);
                    return service.execute(pageableData, request)
                            .fold(
                                    slice -> slice,
                                    () -> new SliceImpl<>(List.of())
                            );
                })
                .then((request, response) -> {
                    assertNotNull(response);
                    assertEquals(2, response.getNumberOfElements());
                    verify(clientRepository, times(1)).findAll(request, pageable);
                    verify(clientRepository, never()).findAll(pageable);
                })
                .execute(pageable);
    }

    @Test
    void getAllPerfectWithNoSpecificationTest() {
        final Pageable pageable = Pageable.ofSize(10);
        this.baseTest
                .given(() -> {
                    when(clientRepository
                            .findAll(any(Pageable.class)))
                            .thenReturn(
                                    new PageImpl<>(List.of(client1, client2)));
                    return null;
                })
                .when((pageableData, request) -> {
                    GetAllClientService service = new GetAllClientService(clientRepository);
                    return service.execute(pageableData, request)
                            .fold(
                                    slice -> slice,
                                    () -> new SliceImpl<>(List.of())
                            );
                })
                .then((request, response) -> {
                    assertNotNull(response);
                    assertEquals(2, response.getNumberOfElements());
                    verify(clientRepository, times(1)).findAll(pageable);
                    verify(clientRepository, never()).findAll(request, pageable);
                })
                .execute(pageable);
    }
}
