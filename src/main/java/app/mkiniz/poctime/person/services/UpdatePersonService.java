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
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class UpdatePersonService implements UpdateBusinessUseCase<Tsid, PersonRequest, PersonResponse> {

    private final PersonRepository personRepository;

    @Override
    public PersonResponse execute(Tsid id, final PersonRequest request) {
        return (PersonResponse) Either.<BusinessException, Context>right(new Context(id, request))
                .flatMap(this::findPersonById)
                .flatMap(this::findDuplicateDocument)
                .flatMap(this::setAndValidatePerson)
                .flatMap(this::updatePerson)
                .map(context -> PersonResponse.fromPerson(context.person))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<? extends BusinessException, ? extends Context> updatePerson(Context context) {
        context.person.updated();
        context.person = personRepository.save(context.person);
        return Either.right(context);

    }

    private Either<? extends BusinessException, ? extends Context> setAndValidatePerson(Context context) {
        context.person.setName(context.request.name());
        context.person.setDocument(context.request.document());
        return context.person.valid().map(validPerson -> context);
    }

    private Either<? extends BusinessException, ? extends Context> findDuplicateDocument(Context context) {
        if (!Objects.deepEquals(context.person.getDocument(), context.request.document()) &&
                personRepository.findByDocument(context.request.document()).isPresent())
            return Either.left(new BusinessException(PersonConstants.DUPLICATED));
        return Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> findPersonById(Context context) {
        Optional<Person> person = personRepository.findById(context.id.toLong());
        person.ifPresent(value -> context.person = value);
        return person.isEmpty() ? Either.left(new BusinessException(PersonConstants.ID_NOT_FOUND)) : Either.right(context);
    }

    static class Context {

        public Context(Tsid id, PersonRequest request) {
            this.request = request;
            this.id = id;
        }

        public Person person;
        public Tsid id;
        public final PersonRequest request;

    }
}
