package app.mkiniz.poctime.person;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.domain.Person;
import com.github.f4b6a3.tsid.Tsid;

import java.util.Optional;

public interface PersonProvider {
    Optional<Person> getPerson(Tsid id);

    Person createPerson(String name, Document<?, ?> document);
}
