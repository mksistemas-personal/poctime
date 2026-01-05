package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.GetAllBaseBusinessTest;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.DefaultQueryContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAllPersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    private GetAllBaseBusinessTest<Specification<Person>, Slice<PersonResponse>> baseTest;

    @BeforeEach
    void setUp() {
        this.baseTest = GetAllBaseBusinessTest.of();
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllPerfectWithSpecificationTest() {
        final Pageable pageable = Pageable.ofSize(10);
        this.baseTest
                .given(() -> {
                    when(personRepository
                            .findAll(any(Specification.class), any(Pageable.class)))
                            .thenReturn(
                                    new PageImpl<>(List.of(
                                            Person.builder().id(1L).name("name-1").build(),
                                            Person.builder().id(2L).name("name-2").build()
                                    )));
                    return new Like<>(new DefaultQueryContext(), "name", "name");
                })
                .when((pageableData, request) -> {
                    GetAllPersonService service = new GetAllPersonService(personRepository);
                    return service.execute(pageableData, request)
                            .fold(
                                    slice -> slice,
                                    () -> new SliceImpl<>(List.of())
                            );
                })
                .then((request, response) -> {
                    assertNotNull(response);
                    assertEquals(2, response.getNumberOfElements());
                    verify(personRepository, times(1)).findAll(request, pageable);
                    verify(personRepository, never()).findAll(pageable);
                })
                .execute(pageable);
    }

    @Test
    void getAllPerfectWithNoSpecificationTest() {
        final Pageable pageable = Pageable.ofSize(10);
        this.baseTest
                .given(() -> {
                    when(personRepository
                            .findAll(any(Pageable.class)))
                            .thenReturn(
                                    new PageImpl<>(List.of(
                                            Person.builder().id(1L).name("name-1").build(),
                                            Person.builder().id(2L).name("name-2").build()
                                    )));
                    return null;
                })
                .when((pageableData, request) -> {
                    GetAllPersonService service = new GetAllPersonService(personRepository);
                    return service.execute(pageableData, request)
                            .fold(
                                    slice -> slice,
                                    () -> new SliceImpl<>(List.of())
                            );
                })
                .then((request, response) -> {
                    assertNotNull(response);
                    assertEquals(2, response.getNumberOfElements());
                    verify(personRepository, times(1)).findAll(pageable);
                    verify(personRepository, never()).findAll(request, pageable);
                })
                .execute(pageable);
    }
}