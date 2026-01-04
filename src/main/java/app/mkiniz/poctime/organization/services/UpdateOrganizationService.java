package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.base.address.Address;
import app.mkiniz.poctime.base.address.AddressCountry;
import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.*;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.person.domain.PersonKindEnumeration;
import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
class UpdateOrganizationService implements UpdateBusinessUseCase<Tsid, UpdateOrganizationRequest, OrganizationResponse> {

    private final OrganizationRepository organizationRepository;
    private final PersonProvider personProvider;
    private final BeanFactory beanFactory;

    @Override
    public OrganizationResponse execute(Tsid id, UpdateOrganizationRequest organizationRequest) {
        return (OrganizationResponse) Either.<BusinessException, Context>right(new Context(id, organizationRequest))
                .flatMap(this::findOrganization)
                .flatMap(this::findResponsiblePerson)
                .flatMap(this::updateAddress)
                .flatMap(this::validateOrganizationPerson)
                .flatMap(this::updateOrganization)
                .map(context -> OrganizationResponse.from(context.organization))
                .fold(this::throwBusinessException, response -> response);
    }

    private Either<BusinessException, Context> validateOrganizationPerson(Context context) {
        OrganizationCountryValidation organizationCountryValidation = beanFactory.getBean(
                OrganizationCountryValidation.getCountry(context.getPersonCountry()), OrganizationCountryValidation.class);
        return organizationCountryValidation.validateOrganizationByCountry(context.organization)
                .map(org -> context);
    }

    private Either<? extends BusinessException, ? extends Context> updateOrganization(Context context) {
        context.organization.setResponsibleEmail(context.request.responsibleEmail());
        return context.organization.valid()
                .map(organization -> {
                    organization.updated();
                    organizationRepository.save(organization);
                    return context;
                });

    }

    private Either<? extends BusinessException, ? extends Context> updateAddress(Context context) {
        AddressCountry addressCountry = beanFactory.getBean(
                AddressCountry.getCountry(context.getPersonCountry()), AddressCountry.class);
        return addressCountry.validate(context.request.address())
                .map(address -> {
                    Address canonizedAddress = addressCountry.canonicalize(address);
                    if (!Objects.deepEquals(context.organization.getAddress(), canonizedAddress))
                        context.organization.setAddress(canonizedAddress);
                    return context;
                });
    }

    private Either<? extends BusinessException, ? extends Context> findResponsiblePerson(Context context) {
        if (!context.getRequestResponsiblePersonIdAsLong().equals(context.getResponsiblePersonIdAsLong())) {
            Optional<Person> responsiblePerson = personProvider.getPerson(context.request.responsiblePersonId());
            if (responsiblePerson.isEmpty())
                return Either.left(new BusinessException(OrganizationConstants.RESPONSIBLE_PERSON_NOT_FOUND));
            else {
                Person responsible = responsiblePerson.get();
                if (responsible.whatKind() != PersonKindEnumeration.CPF)
                    return Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_RESPONSIBLE_PERSON_COUNTRY_WRONG_TYPE));
                context.organization.setResponsiblePerson(responsible);
            }
        }
        return Either.right(context);
    }

    private Either<? extends BusinessException, ? extends Context> findOrganization(Context context) {
        Optional<Organization> org = organizationRepository.findById(context.id.toLong());
        org.ifPresent(value -> context.organization = value);
        return org.isEmpty() ?
                Either.left(new BusinessException(OrganizationConstants.ORGANIZATION_NOT_FOUND)) :
                Either.right(context);
    }

    static class Context {

        public Context(Tsid id, UpdateOrganizationRequest request) {
            this.request = request;
            this.id = id;
        }

        public UpdateOrganizationRequest request;
        public Organization organization;
        public Tsid id;

        public Long getResponsiblePersonIdAsLong() {
            return organization.getResponsiblePerson().getId();
        }

        public Long getRequestResponsiblePersonIdAsLong() {
            return request.responsiblePersonId().toLong();
        }

        public String getPersonCountry() {
            return organization.getPerson().getDocument().getCountry();
        }

    }
}
