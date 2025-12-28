package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class DeletePersonService implements DeleteBusinessUseCase<Tsid, PersonResponse> {

    private final PersonRepository personRepository;

    @Override
    public PersonResponse execute(Tsid id) {
        return Either.<BusinessException, Tsid>right(id)
                .flatMap(identifier -> personRepository.findById(identifier.toLong())
                        .<Either<BusinessException, Person>>map(Either::right)
                        .orElseGet(() -> Either.left(new BusinessException(PersonConstants.ID_NOT_FOUND))))
                .flatMap(person -> {
                    person.deleted();
                    personRepository.delete(person);
                    return Either.right(person);
                })
                .map(PersonResponse::fromPerson)
                .fold(error -> {
                    throw error;
                }, personResponse -> personResponse);
    }
}
