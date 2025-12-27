package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
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
class AddOrganizationService implements AddBusinessUseCase<OrganizationRequest, OrganizationResponse> {

    private final OrganizationRepository organizationRepository;
    private final PersonProvider personProvider;
    private final BeanFactory beanFactory;

    @Override
    public OrganizationResponse execute(OrganizationRequest request) {
        return createContext(request)
                .flatMap(this::findOrganization)
                .flatMap(this::findOrganizationPerson)
                .flatMap(this::validateAddress)
                .flatMap(this::findResponsiblePerson)
                .flatMap(this::createOrganization)
                .flatMap(this::saveOrganization)
                .map(context -> OrganizationResponse.from(context.organization))
                .fold(error -> {
                    throw error;
                }, response -> response);
    }

    private Either<BusinessException, Context> saveOrganization(Context context) {
        context.organization = organizationRepository.save(context.organization);
        return Either.right(context);
    }

    private Either<BusinessException, Context> createOrganization(Context context) {
        Organization organization = Organization.builder()
                .id(context.person.getId())
                .person(context.person)
                .responsiblePerson(context.responsiblePerson)
                .responsibleEmail(context.getResponsibleEmail())
                .address(context.addressCountry.canonicalize(context.getAddress()))
                .build();
        organization.created();
        context.organization = organization;
        return Either.right(context);
    }

    private Either<BusinessException, Context> findResponsiblePerson(Context context) {
        Optional<Person> responsiblePerson = personProvider.getPerson(context.getResponsiblePersonId());
        responsiblePerson.ifPresent(value -> context.responsiblePerson = value);
        return responsiblePerson.isEmpty() ?
                Either.left(new BusinessException(OrganizationConstants.RESPONSIBLE_PERSON_NOT_FOUND)) :
                Either.right(context);
    }

    private Either<BusinessException, Context> validateAddress(Context context) {
        AddressCountry addressCountry = beanFactory.getBean(
                AddressCountry.getCountry(context.getPersonCountry()), AddressCountry.class);
        context.addressCountry = addressCountry;
        return addressCountry.validate(context.getAddress())
                .map(address -> context);
    }

    private Either<BusinessException, Context> findOrganizationPerson(Context context) {
        Optional<Person> person = personProvider.getPerson(context.getPersonId());
        person.ifPresent(value -> context.person = value);
        return person.isEmpty() ?
                Either.left(new BusinessException(OrganizationConstants.PERSON_NOT_FOUND)) :
                Either.right(context);
    }

    Either<BusinessException, Context> createContext(OrganizationRequest request) {
        return Either.right(Context.of(request));
    }

    Either<BusinessException, Context> findOrganization(Context context) {
        Optional<Organization> org = organizationRepository.findById(context.getPersonIdAsLong());
        return org.isEmpty() ?
                Either.right(context) :
                Either.left(new BusinessException(OrganizationConstants.DUPLICATED));
    }


    static class Context {
        public OrganizationRequest request;
        public Organization organization;
        public Person person;
        public Person responsiblePerson;
        public AddressCountry addressCountry;

        private Context(OrganizationRequest request) {
            this.request = request;
        }

        public static Context of(OrganizationRequest request) {
            return new Context(request);
        }

        public Long getPersonIdAsLong() {
            return request.personId().toLong();
        }

        public Tsid getPersonId() {
            return request.personId();
        }

        public String getPersonCountry() {
            return person.getDocument().getCountry();
        }

        public Address getAddress() {
            return request.address();
        }

        public Tsid getResponsiblePersonId() {
            return request.responsiblePersonId();
        }

        public String getResponsibleEmail() {
            return request.responsibleEmail();
        }
    }

}
