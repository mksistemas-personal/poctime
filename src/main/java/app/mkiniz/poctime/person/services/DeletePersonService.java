package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
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
        Person person = personRepository.findById(id.toLong()).orElseThrow(() -> new BusinessException(PersonConstants.ID_NOT_FOUND));
        person.deleted();
        personRepository.delete(person);
        return PersonResponse.fromPerson(person);
    }
}
