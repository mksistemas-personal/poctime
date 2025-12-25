package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.UpdateBaseBusinessTest;
import app.mkiniz.poctime.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePersonServiceTest {

    public static final String NAME = "name";
    public static final CpfDocument CPF_DOCUMENT = new CpfDocument("123.456.789-00");
    public static final String NAME_UPDATED = "name-updated";
    public static final CpfDocument CPF_DOCUMENT_UPDATED = new CpfDocument("076.161.130-44");
    public static final CpfDocument INVALID_CPF_DOCUMENT = new CpfDocument("076.161.130-45");

    @Mock
    private PersonRepository personRepository;

    @Captor
    private ArgumentCaptor<Person> personCaptor;

    private UpdateBaseBusinessTest<Tsid, PersonRequest, PersonResponse> baseTest;

    @BeforeEach
    void setUp() {
        this.baseTest = UpdateBaseBusinessTest.<Tsid, PersonRequest, PersonResponse>of();
    }

    @Test
    void updatePerfectTest() {
        this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong()))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT).build()));
                    when(personRepository.findByDocument(any(CpfDocument.class)))
                            .thenReturn(Optional.empty());
                    when(personRepository.save(any()))
                            .thenReturn(Person.builder().id(1L).name(NAME_UPDATED).document(CPF_DOCUMENT_UPDATED).build());
                    return new PersonRequest(NAME_UPDATED, CPF_DOCUMENT_UPDATED);
                })
                .when(() -> new UpdatePersonService(personRepository))
                .then((request, response) -> {
                    verify(personRepository, times(1)).save(personCaptor.capture());
                    verify(personRepository, times(1)).findById(anyLong());
                    verify(personRepository, times(1)).findByDocument(CPF_DOCUMENT_UPDATED);
                    assertNotNull(response);
                    assertEquals(Tsid.from(1L).toLowerCase(), response.id());
                    assertEquals(NAME_UPDATED, response.name());
                    assertEquals(CPF_DOCUMENT_UPDATED, response.document());
                    assertEquals(1L, personCaptor.getValue().getId());
                    assertEquals(NAME_UPDATED, personCaptor.getValue().getName());
                    assertEquals(CPF_DOCUMENT_UPDATED, personCaptor.getValue().getDocument());

                })
                .execute(Tsid.from(1L));
    }

    @Test
    void updatePerfectWithSameDocumentTest() {
        this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong()))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT_UPDATED).build()));
                    when(personRepository.save(any()))
                            .thenReturn(Person.builder().id(1L).name(NAME_UPDATED).document(CPF_DOCUMENT_UPDATED).build());
                    return new PersonRequest(NAME_UPDATED, CPF_DOCUMENT_UPDATED);
                })
                .when(() -> new UpdatePersonService(personRepository))
                .then((request, response) -> {
                    verify(personRepository, times(1)).save(personCaptor.capture());
                    verify(personRepository, times(1)).findById(anyLong());
                    verify(personRepository, times(0)).findByDocument(CPF_DOCUMENT_UPDATED);
                    assertNotNull(response);
                    assertEquals(Tsid.from(1L).toLowerCase(), response.id());
                    assertEquals(NAME_UPDATED, response.name());
                    assertEquals(CPF_DOCUMENT_UPDATED, response.document());
                    assertEquals(1L, personCaptor.getValue().getId());
                    assertEquals(NAME_UPDATED, personCaptor.getValue().getName());
                    assertEquals(CPF_DOCUMENT_UPDATED, personCaptor.getValue().getDocument());

                })
                .execute(Tsid.from(1L));
    }

    @Test
    void notFoundTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
                    return new PersonRequest(NAME_UPDATED, CPF_DOCUMENT_UPDATED);
                })
                .when(() -> new UpdatePersonService(personRepository))
                .then((request, response) -> {

                })
                .execute(Tsid.from(2L)));
        verify(personRepository, never()).save(any());
        assertEquals(PersonConstants.ID_NOT_FOUND, exception.getMessage());
    }

    @Test
    void updateInvalidDocumentTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong()))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT).build()));
                    return new PersonRequest(NAME_UPDATED, INVALID_CPF_DOCUMENT);
                })
                .when(() -> new UpdatePersonService(personRepository))
                .then((request, response) -> {
                })
                .execute(Tsid.from(1L)));
        verify(personRepository, times(0)).save(any(Person.class));
        verify(personRepository, times(1)).findById(anyLong());
        assertEquals(PersonConstants.DOCUMENT_INVALID, exception.getMessage());
    }

    @Test
    void updateDuplicatedDocumentTest() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong()))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT).build()));
                    when(personRepository.findByDocument(CPF_DOCUMENT_UPDATED))
                            .thenReturn(Optional.of(Person.builder().id(1L).name(NAME).document(CPF_DOCUMENT_UPDATED).build()));
                    return new PersonRequest(NAME_UPDATED, CPF_DOCUMENT_UPDATED);
                })
                .when(() -> new UpdatePersonService(personRepository))
                .then((request, response) -> {
                })
                .execute(Tsid.from(1L)));
        verify(personRepository, times(0)).save(any(Person.class));
        verify(personRepository, times(1)).findById(anyLong());
        verify(personRepository, times(1)).findByDocument(CPF_DOCUMENT_UPDATED);
        assertEquals(PersonConstants.DUPLICATED, exception.getMessage());
    }
}