package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
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
    public PersonResponse execute(Tsid id, PersonRequest request) {
        Person person = personRepository.findById(id.toLong())
                .orElseThrow(() -> new BusinessException(PersonConstants.ID_NOT_FOUND));
        if (!Objects.deepEquals(person.getDocument(), request.document()) && personRepository.findByDocument(request.document()).isPresent())
            throw new BusinessException(PersonConstants.DUPLICATED);
        person.setName(request.name());
        person.setDocument(request.document());
        person.valid();
        person.updated();
        personRepository.save(person);
        return PersonResponse.fromPerson(person);
    }
}
