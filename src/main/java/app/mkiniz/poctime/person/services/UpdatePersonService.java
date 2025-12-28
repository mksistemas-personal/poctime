package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
class UpdatePersonService implements UpdateBusinessUseCase<Tsid, PersonRequest, PersonResponse> {

    private final PersonRepository personRepository;

    @Override
    public PersonResponse execute(Tsid id, final PersonRequest request) {
        return Either.<BusinessException, PersonRequest>right(request)
                .flatMap(requestData -> personRepository.findById(id.toLong())
                        .<Either<BusinessException, Person>>map(Either::right)
                        .orElseGet(() -> Either.left(new BusinessException(PersonConstants.ID_NOT_FOUND))))
                .flatMap(person -> {
                    if (!Objects.deepEquals(person.getDocument(), request.document()) &&
                            personRepository.findByDocument(request.document()).isPresent())
                        return Either.left(new BusinessException(PersonConstants.DUPLICATED));
                    return Either.right(person);
                })
                .flatMap(person -> {
                    person.setName(request.name());
                    person.setDocument(request.document());
                    return person.valid().map(validPerson -> validPerson);
                })
                .flatMap(person -> {
                    person.updated();
                    return Either.right(personRepository.save(person));
                })
                .map(PersonResponse::fromPerson)
                .fold(error -> {
                    throw error;
                }, response -> response);
    }
}
