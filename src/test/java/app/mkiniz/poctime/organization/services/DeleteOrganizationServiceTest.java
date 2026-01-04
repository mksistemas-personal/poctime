package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
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
class DeleteOrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private TsidGenerator generator;

    @InjectMocks
    private DeleteOrganizationService deleteOrganizationService;

    private DeleteBaseBusinessTest<Tsid, OrganizationResponse> baseTest;
    private Tsid orgId;
    private Organization organization;
    private OrganizationResponse organizationResponse;

    @BeforeEach
    void setUp() {
        this.baseTest = DeleteBaseBusinessTest.of();
        Tsid personId = generator.newTsid();
        Tsid responsibleId = generator.newTsid();
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
        String responsibleEmail = "responsible@example.com";
        Person person = Person.builder()
                .id(personId.toLong())
                .name("John Doe")
                .document(new CnpjDocument("45335153000134"))
                .build();
        Person responsiblePerson = Person.builder()
                .id(responsibleId.toLong())
                .name("Mary Who")
                .document(new CpfDocument("45212890052"))
                .build();
        this.organization = Organization.builder()
                .id(personId.toLong())
                .deleted(false)
                .address(address)
                .responsiblePerson(responsiblePerson)
                .responsibleEmail(responsibleEmail)
                .person(person)
                .build();
        orgId = Tsid.from(organization.getId());
        this.organizationResponse = OrganizationResponse.from(organization);
    }

    @Test
    void deleteOrganizationPerfectTest() {
        this.baseTest
                .given(() -> {
                    when(organizationRepository.findById(orgId.toLong())).thenReturn(Optional.of(organization));
                    doNothing().when(organizationRepository).delete(organization);
                    return orgId;
                })
                .when((id) -> deleteOrganizationService.execute(id))
                .then((id, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response).isEqualTo(organizationResponse);
                    verify(organizationRepository, times(1)).delete(organization);
                    verify(organizationRepository, times(1)).findById(orgId.toLong());
                })
                .execute();
    }
}