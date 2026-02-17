package app.mkiniz.poctime.client.services;

import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.client.ClientConstants;
import app.mkiniz.poctime.client.domain.Client;
import app.mkiniz.poctime.client.domain.ClientRepository;
import app.mkiniz.poctime.client.domain.ClientRequest;
import app.mkiniz.poctime.client.domain.ClientResponse;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class AddClientServices implements AddBusinessUseCase<ClientRequest, ClientResponse> {

    private final ClientRepository clientRepository;
    private final PersonProvider personProvider;
    private final BeanFactory beanFactory;

    @Override
    public ClientResponse execute(ClientRequest clientRequest) {
        return createContext(clientRequest)
                .flatMap(this::findClient)
                .flatMap(this::findClientPerson)
                .flatMap(this::validateAddress)
                .flatMap(this::createClient)
                .flatMap(this::saveClient)
                .map(context -> ClientResponse.from(context.client))
                .fold(error -> {
                    throw error;
                }, response -> response);
    }

    private Either<BusinessException, Context> saveClient(Context context) {
        context.client.created();
        context.client = clientRepository.save(context.client);
        return Either.right(context);
    }

    private Either<BusinessException, Context> createClient(Context context) {
        context.client = Client.builder()
                .id(new TsidGenerator().newIdAsLong())
                .person(context.person)
                .clientEmail(context.request.clientEmail())
                .address(context.addressCountry.canonicalize(context.request.address()))
                .build();
        return Either.right(context);
    }

    private Either<BusinessException, Context> validateAddress(Context context) {
        AddressCountry addressCountry = beanFactory.getBean(
                AddressCountry.getCountry(context.getPersonCountry()), AddressCountry.class);
        context.addressCountry = addressCountry;
        return addressCountry.validate(context.request.address())
                .map(address -> context);
    }

    private Either<BusinessException, Context> findClientPerson(Context context) {
        Optional<Person> person = context.isNewPerson() ? Optional.of(personProvider.createPerson(
                context.request.person().name(),
                context.request.person().document()
        )) : personProvider.getPerson(context.getPersonId());
        person.ifPresent(value -> context.person = value);
        return person.isEmpty() ?
                Either.left(new BusinessException(ClientConstants.PERSON_NOT_FOUND)) :
                Either.right(context);
    }

    private Either<BusinessException, Context> createContext(ClientRequest request) {
        return Either.right(Context.of(request));
    }

    private Either<BusinessException, Context> findClient(Context context) {
        if (context.isNewPerson())
            return Either.right(context);
        Optional<Client> client = clientRepository.findByPersonId(context.getPersonIdAsLong());
        return client.isEmpty() ?
                Either.right(context) :
                Either.left(new BusinessException(ClientConstants.DUPLICATED));
    }

    static class Context {
        public ClientRequest request;
        public Client client;
        public Person person;
        public AddressCountry addressCountry;

        private Context(ClientRequest request) {
            this.request = request;
        }

        public static Context of(ClientRequest request) {
            return new Context(request);
        }

        public boolean isNewPerson() {
            return request.person().isNew();
        }

        public Long getPersonIdAsLong() {
            return TsidGenerator.fromStringToLong(request.person().id());
        }

        public Tsid getPersonId() {
            return Tsid.from(request.person().id());
        }

        public String getPersonCountry() {
            return person.getDocument().getCountry();
        }
    }
}
