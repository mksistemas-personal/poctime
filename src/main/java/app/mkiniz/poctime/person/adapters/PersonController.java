package app.mkiniz.poctime.person.adapters;

import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/person")
@AllArgsConstructor
@Validated
public class PersonController {

    private final AddBusinessUseCase<PersonRequest, PersonResponse> addPersonService;
    private final UpdateBusinessUseCase<Tsid, PersonRequest, PersonResponse> updatePersonService;
    private final DeleteBusinessUseCase<Tsid, PersonResponse> deletePersonService;
    private final GetByIdBusinessUseCase<Tsid, PersonResponse> getPersonByIdService;
    private final GetAllBusinessUseCase<Specification<Person>, Maybe<Slice<PersonResponse>>> getAllPersonService;

    @PostMapping
    public PersonResponse createPerson(@Valid @RequestBody PersonRequest request) {
        return addPersonService.execute(request);
    }

    @PutMapping(path = "/{id}")
    public PersonResponse updatePerson(@PathVariable("id") Tsid id, @RequestBody PersonRequest request) {
        return updatePersonService.execute(id, request);
    }

    @DeleteMapping(path = "/{id}")
    public PersonResponse deletePerson(@PathVariable("id") Tsid id) {
        return deletePersonService.execute(id);
    }

    @GetMapping(path = "/{id}")
    public PersonResponse getPersonById(@PathVariable("id") Tsid id) {
        return getPersonByIdService.execute(id);
    }

    @GetMapping
    public ResponseEntity<Slice<PersonResponse>> getAllPeople(
            @And({
                    @Spec(path = "name", params = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "identifier", params = "identifier", spec = EqualIgnoreCase.class)
            }) Specification<Person> spec, Pageable pageable) {
        return getAllPersonService.execute(pageable, spec)
                .fold(
                        ResponseEntity::ok,
                        () -> ResponseEntity.noContent().build()
                );
    }

}
