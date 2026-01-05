package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.organization.OrganizationProvider;
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
    private final OrganizationProvider organizationProvider;

    @Override
    public PersonResponse execute(Tsid id) {
        return (PersonResponse) Either.<BusinessException, Tsid>right(id)
                .flatMap(this::findPerson)
                .flatMap(this::canRemovePerson)
                .flatMap(this::deletePerson)
                .map(PersonResponse::fromPerson)
                .fold(this::throwBusinessException, personResponse -> personResponse);
    }

    private Either<BusinessException, Person> findPerson(Tsid identifier) {
        return personRepository.findById(identifier.toLong())
                .<Either<BusinessException, Person>>map(Either::right)
                .orElseGet(() -> Either.left(new BusinessException(PersonConstants.ID_NOT_FOUND)));
    }

    private Either<? extends BusinessException, ? extends Person> deletePerson(Person person) {
        person.deleted();
        personRepository.delete(person);
        return Either.right(person);
    }

    private Either<? extends BusinessException, Person> canRemovePerson(Person person) {
        if (organizationProvider.canRemovePerson(Tsid.from(person.getId())))
            return Either.right(person);
        return Either.left(new BusinessException(PersonConstants.CANNOT_REMOVE_PERSON_ORGANIZATION));
    }
}
