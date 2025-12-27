package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.base.address.BrazilianAddress;
import app.mkiniz.poctime.base.document.bra.CnpjDocument;
import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.AddBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddOrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private PersonProvider personProvider;

    @InjectMocks
    private TsidGenerator tsidGenerator;

    @Mock
    private BeanFactory beanFactory;

    @InjectMocks
    private AddOrganizationService addOrganizationService;

    @Captor
    private ArgumentCaptor<Organization> organizationCaptor;

    private AddBaseBusinessTest<OrganizationRequest, OrganizationResponse> baseTest;
    private Tsid personId;
    private Tsid responsibleId;
    private Address address;
    private String responsibleEmail;
    private Person person;
    private Person responsiblePerson;
    private Organization organization;
    private Address canonizedAddress;

    @BeforeEach
    void setUp() {
        this.baseTest = AddBaseBusinessTest.<OrganizationRequest, OrganizationResponse>of();
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
    }

    @Test
    void addOrganizationPerfectTest() {
        this.baseTest
                .given(() -> {
                    when(personProvider.getPerson(personId)).thenReturn(Optional.of(person));
                    when(personProvider.getPerson(responsibleId)).thenReturn(Optional.of(responsiblePerson));
                    when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());
                    when(organizationRepository.save(any(Organization.class)))
                            .thenAnswer(invocation -> invocation.getArgument(0));
                    when(beanFactory.getBean("address-br", AddressCountry.class)).thenReturn(new BrazilianAddress());
                    return new OrganizationRequest(personId, address, responsibleId, responsibleEmail);
                })
                .when(request -> addOrganizationService.execute(request))
                .then((request, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response.organizationId()).isEqualTo(personId);
                    assertThat(response.address()).isEqualTo(address);
                    assertThat(response.responsibleId()).isEqualTo(responsibleId);
                    assertThat(response.responsibleEmail()).isEqualTo(responsibleEmail);
                    verify(organizationRepository).save(organizationCaptor.capture());
                    assertThat(organizationCaptor.getValue())
                            .usingRecursiveComparison()
                            .ignoringFields("domainEvents")
                            .isEqualTo(organization);
                    verify(organizationRepository, times(1)).findById(personId.toLong());
                    verify(personProvider, times(1)).getPerson(personId);
                    verify(personProvider, times(1)).getPerson(responsibleId);
                    verify(beanFactory, times(1)).getBean("address-br", AddressCountry.class);
                    verify(organizationRepository, times(1)).save(any(Organization.class));
                })
                .execute();
    }

    @Test
    void addOrganizationWithOrganizationTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(organizationRepository.findById(anyLong())).thenReturn(Optional.of(organization));
                    return new OrganizationRequest(personId, address, responsibleId, responsibleEmail);
                })
                .when(request -> addOrganizationService.execute(request))
                .then((request, response) -> {
                    fail();
                })
                .execute()
        );
        assertThat(exception.getMessage()).isEqualTo(OrganizationConstants.DUPLICATED);
    }

    @Test
    void addOrganizationWithNoPersonTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());
                    when(personProvider.getPerson(personId)).thenReturn(Optional.empty());
                    return new OrganizationRequest(personId, address, responsibleId, responsibleEmail);
                })
                .when(request -> addOrganizationService.execute(request))
                .then((request, response) -> {
                    fail();
                })
                .execute()
        );
        assertThat(exception.getMessage()).isEqualTo(OrganizationConstants.PERSON_NOT_FOUND);
    }

    @Test
    void addOrganizationWithNoResponsiblePersonTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(organizationRepository.findById(anyLong())).thenReturn(Optional.empty());
                    when(personProvider.getPerson(personId)).thenReturn(Optional.of(person));
                    when(personProvider.getPerson(responsibleId)).thenReturn(Optional.empty());
                    when(beanFactory.getBean("address-br", AddressCountry.class)).thenReturn(new BrazilianAddress());
                    return new OrganizationRequest(personId, address, responsibleId, responsibleEmail);
                })
                .when(request -> addOrganizationService.execute(request))
                .then((request, response) -> {
                    fail();
                })
                .execute()
        );
        assertThat(exception.getMessage()).isEqualTo(OrganizationConstants.RESPONSIBLE_PERSON_NOT_FOUND);
    }

}