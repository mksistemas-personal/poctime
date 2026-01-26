package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.base.address.BrazilianAddress;
import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.organization.domain.*;
import app.mkiniz.poctime.organization.domain.countries.BrazilianOrganizationCountryValidation;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.UpdateBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.BeanFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateOrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PersonProvider personProvider;

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private UpdateOrganizationService updateOrganizationService;

    @Captor
    private ArgumentCaptor<Organization> organizationCaptor;

    private UpdateBaseBusinessTest<Tsid, UpdateOrganizationRequest, OrganizationResponse> baseTest;
    private Tsid personId;
    private Tsid responsibleId;
    private Address address;
    private String responsibleEmail;
    private Person person;
    private Person responsiblePerson;
    private Organization organization;
    private Address canonizedAddress;
    private Address newAddress;
    private Address canonizedNewAddress;
    private String newResponsibleEmail;
    private Tsid newResponsibleId;
    private Person newResponsiblePerson;
    private PersonRequest personRequest;
    private PersonRequest personResponsibleRequest;

    @BeforeEach
    void setUp() {
        this.baseTest = UpdateBaseBusinessTest.of();
        TsidGenerator generator = new TsidGenerator();
        this.personId = generator.newTsid();
        this.responsibleId = generator.newTsid();
        this.address = Address.builder()
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
        this.canonizedAddress = new BrazilianAddress().canonicalize(address);
        this.responsibleEmail = "responsible@example.com";
        this.person = Person.builder()
                .id(personId.toLong())
                .name("John Doe")
                .document(new CnpjDocument("45335153000134"))
                .build();
        this.responsiblePerson = Person.builder()
                .id(responsibleId.toLong())
                .name("Mary Who")
                .document(new CpfDocument("45212890052"))
                .build();
        this.organization = Organization.builder()
                .id(personId.toLong())
                .deleted(false)
                .address(canonizedAddress)
                .responsiblePerson(responsiblePerson)
                .responsibleEmail(responsibleEmail)
                .person(person)
                .build();
        this.newAddress = Address.builder()
                .state("Santa Catarina")
                .city("Lages")
                .stateCode("SC")
                .street("Avenida Belisario Ramos")
                .zipCode("88596-100")
                .complement("Apto")
                .neighborhood("Guadalupe")
                .number("3175")
                .country("BR")
                .build();
        this.canonizedNewAddress = new BrazilianAddress().canonicalize(newAddress);
        this.newResponsibleEmail = "responsible@example.com.br";
        this.newResponsibleId = generator.newTsid();
        this.newResponsiblePerson = Person.builder()
                .id(newResponsibleId.toLong())
                .name("Mary Who 2")
                .document(new CpfDocument("56877367006"))
                .build();
        this.personRequest = PersonRequest.builder()
                .name("John Doe")
                .document(new CnpjDocument("45335153000134"))
                .build();
        this.personResponsibleRequest = PersonRequest.builder()
                .id(newResponsibleId.toLowerCase())
                .name("Mary Who")
                .document(new CnpjDocument("45212890052"))
                .build();
    }

    @Test
    void UpdateOrganizationPerfect() {
        this.baseTest
                .given(() -> {
                    when(organizationRepository.findById(anyLong())).thenReturn(Optional.of(organization));
                    when(personProvider.getPerson(newResponsibleId)).thenReturn(Optional.of(newResponsiblePerson));
                    when(beanFactory.getBean("address-br", AddressCountry.class)).thenReturn(new BrazilianAddress());
                    when(beanFactory.getBean("organization-country-br", OrganizationCountryValidation.class)).thenReturn(new BrazilianOrganizationCountryValidation());
                    return new UpdateOrganizationRequest(newAddress, personResponsibleRequest, newResponsibleEmail);
                })
                .when((id, request) -> updateOrganizationService.execute(id, request))
                .then((id, request, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response.organizationId()).isEqualTo(personId);
                    assertThat(response.address()).isEqualTo(newAddress);
                    assertThat(response.responsiblePerson().id()).isEqualTo(newResponsibleId.toLowerCase());
                    assertThat(response.responsibleEmail()).isEqualTo(newResponsibleEmail);
                    verify(organizationRepository, times(1)).findById(id.toLong());
                    verify(personProvider, times(1)).getPerson(newResponsibleId);
                    verify(beanFactory, times(1)).getBean("address-br", AddressCountry.class);
                    verify(beanFactory, times(1)).getBean("organization-country-br", OrganizationCountryValidation.class);
                    verify(organizationRepository, times(1)).save(any(Organization.class));
                })
                .execute(personId);
    }
}