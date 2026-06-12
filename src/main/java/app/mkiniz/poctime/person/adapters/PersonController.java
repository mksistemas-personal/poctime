package app.mkiniz.poctime.person.adapters;

import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.person.domain.PersonSearchRequest;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final GetAllBusinessUseCase<PersonSearchRequest, Maybe<Slice<PersonResponse>>> getAllPersonService;

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
            PersonSearchRequest request, Pageable pageable) {
        return getAllPersonService.execute(pageable, request)
                .fold(
                        ResponseEntity::ok,
                        () -> ResponseEntity.noContent().build()
                );
    }

}
