package app.mkiniz.poctime.person.services;

import app.mkiniz.poctime.person.domain.PersonRepository;
import app.mkiniz.poctime.person.domain.PersonResponse;
import app.mkiniz.poctime.person.domain.PersonSearchRequest;
import app.mkiniz.poctime.shared.business.GetAllBusinessUseCase;
import cyclops.control.Maybe;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cyclops.control.Eval.later;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetAllPersonService implements GetAllBusinessUseCase<PersonSearchRequest, Maybe<Slice<PersonResponse>>> {

    private final PersonRepository personRepository;

    @Override
    public Maybe<Slice<PersonResponse>> execute(Pageable pageable, @Nullable PersonSearchRequest request) {
        return Maybe.fromEval(later(() -> personRepository.findBySearchRequest(request, pageable)))
                .filter(Slice::hasContent)
                .map(people -> new SliceImpl<>(people.map(PersonResponse::fromPerson).toList(),
                        pageable,
                        people.hasNext()));
    }
}
