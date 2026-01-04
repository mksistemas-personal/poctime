package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class AddPersonService implements AddBusinessUseCase<PersonRequest, PersonResponse> {

    private final PersonRepository personRepository;
    private final TsidGenerator tsidGenerator;

    @Override
    public PersonResponse execute(PersonRequest request) {
        return (PersonResponse) Either.<BusinessException, Context>right(new Context(request))
                .flatMap(this::findDuplicateDocument)
                .flatMap(this::createPerson)
                .flatMap(context -> context.person.valid().map(org -> context))
                .flatMap(this::savePerson)
                .map(context -> PersonResponse.fromPerson(context.person))
                .fold(this::throwBusinessException, person -> person);
    }

    private Either<? extends BusinessException, ? extends Context> findDuplicateDocument(Context context) {
        Optional<Person> person = personRepository.findByDocument(context.request.document());
        return person.isEmpty() ? Either.right(context) : Either.left(new BusinessException(PersonConstants.DUPLICATED));
    }

    private Either<? extends BusinessException, ? extends Context> createPerson(Context context) {
        context.person = Person.builder()
                .id(tsidGenerator.newIdAsLong())
                .name(context.request.name())
                .document(context.request.document())
                .build();
        return Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> savePerson(Context context) {
        context.person.created();
        context.person = personRepository.save(context.person);
        return Either.right(context);
    }

    private static class Context {
        public Person person;
        public final PersonRequest request;

        public Context(PersonRequest request) {
            this.request = request;
        }
    }

}
