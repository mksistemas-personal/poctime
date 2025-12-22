package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.PersonConstants;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class AddPersonService implements AddBusinessUseCase<PersonRequest, PersonResponse> {

    private final PersonRepository personRepository;
    private final TsidGenerator tsidGenerator;

    @Override
    public PersonResponse execute(PersonRequest request) {
        return AddBusinessDefaultProcess.<PersonRequest, PersonResponse, Person>builder()
                .duplicateSupplier(() -> personRepository.findByDocument(request.document()))
                .duplicatedMessage(PersonConstants.DUPLICATED)
                .newEntitySupplier(() -> Person.builder()
                        .id(tsidGenerator.newIdAsLong())
                        .name(request.name())
                        .document(request.document())
                        .build())
                .entitySaver(personRepository::save)
                .responseSupplier(PersonResponse::fromPerson)
                .businessEntityConsumer((person, personRequest) -> person.valid())
                .build()
                .execute(request);


    }
}
