package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.AddBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddPersonServiceTest {

    public static final String NAME = "name";
    public static final CpfDocument CPF_DOCUMENT = new CpfDocument("941.261.500-05");
    public static final CpfDocument INVALID_CPF_DOCUMENT = new CpfDocument("941.261.500-04");

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private TsidGenerator tsidGenerator;

    private AddBaseBusinessTest<PersonRequest, PersonResponse> baseTest;

    @BeforeEach
    void setUp() {
        this.baseTest = AddBaseBusinessTest.<PersonRequest, PersonResponse>of();
    }

    @Test
    void addPerfectTest() {
        this.baseTest
                .given(() -> {
                    when(personRepository.findByDocument(any(CpfDocument.class)))
                            .thenReturn(Optional.empty());
                    when(personRepository.save(any()))
                            .thenReturn(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT).build());
                    return new PersonRequest(NAME, CPF_DOCUMENT);
                })
                .when(() -> new AddPersonService(personRepository, tsidGenerator))
                .then((request, response) -> {
                    verify(personRepository, times(1)).save(any());
                    verify(personRepository, times(1)).findByDocument(any(CpfDocument.class));
                    assertNotNull(response);
                    assertEquals(Tsid.from(1L).toLowerCase(), response.id());
                    assertEquals(NAME, response.name());
                    assertEquals(CPF_DOCUMENT, response.document());
                })
                .execute();
    }

    @Test
    void duplicatedTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findByDocument(any(CpfDocument.class)))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT).build()));
                    return new PersonRequest(NAME, CPF_DOCUMENT);
                })
                .when(() -> new AddPersonService(personRepository, tsidGenerator))
                .then((request, response) -> {
                })
                .execute()
        );
        assertEquals(PersonConstants.DUPLICATED, exception.getMessage());
        verify(personRepository, never()).save(any(Person.class));
    }

    @Test
    void addPDocumentInvalidTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findByDocument(any(CpfDocument.class)))
                            .thenReturn(Optional.empty());
                    return new PersonRequest(NAME, INVALID_CPF_DOCUMENT);
                })
                .when(() -> new AddPersonService(personRepository, tsidGenerator))
                .then((request, response) -> {
                })
                .execute());
        verify(personRepository, times(0)).save(any());
        verify(personRepository, times(1)).findByDocument(any(CpfDocument.class));
        assertEquals(PersonConstants.DOCUMENT_INVALID, exception.getMessage());
    }

}