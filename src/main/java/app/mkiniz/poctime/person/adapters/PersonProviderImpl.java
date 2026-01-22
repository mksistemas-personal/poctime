package app.mkiniz.poctime.person.adapters;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
class PersonProviderImpl implements PersonProvider {

    private final PersonRepository personRepository;
    private final AddBusinessUseCase<PersonRequest, PersonResponse> addPersonService;

    @Override
    public Optional<Person> getPerson(Tsid id) {
        return personRepository.findById(id.toLong());
    }

    @Override
    public Person createPerson(String name, Document<?, ?> document) {
        PersonResponse response = addPersonService.execute(new PersonRequest(name, document));
        return response.toPerson();
    }
}
