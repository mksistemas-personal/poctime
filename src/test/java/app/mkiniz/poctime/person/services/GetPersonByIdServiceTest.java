package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.GetByIdBaseBusinessTest;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPersonByIdServiceTest {

    @Mock
    private PersonRepository personRepository;

    private GetByIdBaseBusinessTest<Tsid, PersonResponse> baseTest;
    private CpfDocument cpfDocument;

    @BeforeEach
    void setUp() {
        this.baseTest = GetByIdBaseBusinessTest.<Tsid, PersonResponse>of();
        this.cpfDocument = new CpfDocument("123.456.789-00");
    }

    @Test
    void getPersonByIdPerfectTest() {
        this.baseTest
                .given(() -> {
                    when(personRepository
                            .findById(anyLong()))
                            .thenReturn(
                                    Optional.of(Person.builder().id(1L).name("name-get").document(cpfDocument).build())
                            );
                    return Tsid.from(1L);
                })
                .when(() -> new GetPersonByIdService(personRepository))
                .then((tsid, response) -> {
                    assertNotNull(response);
                    verify(personRepository, times(1)).findById(anyLong());
                    assertEquals(tsid.toLowerCase(), response.id());
                    assertEquals("name-get", response.name());
                    assertEquals(cpfDocument, response.document());
                })
                .execute();
    }

    @Test
    void deleteIdNotFound() {
        BusinessException exception = assertThrows(BusinessException.class, () -> this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong())).thenReturn(Optional.empty());
                    return Tsid.from(1L);
                })
                .when(() -> new GetPersonByIdService(personRepository))
                .execute());
        assertNotNull(exception);
        assertEquals(PersonConstants.ID_NOT_FOUND, exception.getMessage());
    }
}