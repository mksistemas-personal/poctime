package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@AllArgsConstructor
@Component
class PersonProviderImpl implements PersonProvider {

    private final PersonRepository personRepository;

    @Override
    public Optional<Person> getPerson(Tsid id) {
        return personRepository.findById(id.toLong());
    }
}
