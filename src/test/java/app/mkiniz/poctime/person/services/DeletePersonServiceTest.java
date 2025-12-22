package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.base.document.bra.CpfDocument;
import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.base.BusinessException;
import app.mkiniz.poctime.shared.DeleteBaseBusinessTest;
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
class DeletePersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    private DeleteBaseBusinessTest<Tsid, PersonResponse> baseTest;
    private CpfDocument cpfDocument;

    @BeforeEach
    void setUp() {
        this.baseTest = DeleteBaseBusinessTest.<Tsid, PersonResponse>of();
        this.cpfDocument = new CpfDocument("123.456.789-00");
    }

    @Test
    void deletePerfectTest() {
        this.baseTest
                .given(() -> {
                    when(personRepository.findById(anyLong())).thenReturn(
                            Optional.of(
                                    Person.builder().id(1L).name("name-del").document(cpfDocument).build())
                    );
                    return Tsid.from(1L);
                })
                .when(() -> new DeletePersonService(personRepository))
                .then((tsid, response) -> {
                    assertNotNull(response);
                    assertEquals(tsid.toLowerCase(), response.id());
                    assertEquals("name-del", response.name());
                    assertEquals(cpfDocument, response.document());
                    verify(personRepository, times(1)).findById(anyLong());
                    verify(personRepository, times(1)).delete(any(Person.class));
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
                .when(() -> new DeletePersonService(personRepository))
                .execute());
        assertNotNull(exception);
        assertEquals(PersonConstants.ID_NOT_FOUND, exception.getMessage());
    }

}