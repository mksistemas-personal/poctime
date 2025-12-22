package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.business.base.GetAllBusinessUseCase;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllPersonService implements GetAllBusinessUseCase<Specification<Person>, Slice<PersonResponse>> {

    private final PersonRepository personRepository;

    @Override
    public Slice<PersonResponse> execute(Pageable pageable, @Nullable Specification<Person> personSpecification) {
        Slice<Person> people = Objects.nonNull(personSpecification) ?
                personRepository.findAll(personSpecification, pageable) :
                personRepository.findAll(pageable);
        return new SliceImpl<>(people.map(PersonResponse::fromPerson).toList(), pageable, people.hasNext());
    }
}
