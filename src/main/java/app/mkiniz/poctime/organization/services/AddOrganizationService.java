package app.mkiniz.poctime.organization.services;

import app.mkiniz.poctime.organization.OrganizationConstants;
import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRepository;
import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.person.PersonProvider;
import app.mkiniz.poctime.person.domain.Person;
import app.mkiniz.poctime.shared.business.address.AddressCountry;
import app.mkiniz.poctime.shared.business.base.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.base.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
class AddOrganizationService implements AddBusinessUseCase<OrganizationRequest, OrganizationResponse> {

    private final OrganizationRepository organizationRepository;
    private final PersonProvider personProvider;
    private final BeanFactory beanFactory;

    @Override
    public OrganizationResponse execute(OrganizationRequest request) {
        organizationRepository.findById(request.personId().toLong())
                .orElseThrow(() -> new BusinessException(OrganizationConstants.DUPLICATED));
        Person person = personProvider.getPerson(request.personId())
                .orElseThrow(() -> new BusinessException(OrganizationConstants.PERSON_NOT_FOUND));
        AddressCountry addressValidator = beanFactory.getBean(
                AddressCountry.getCountry(person.getDocument().getCountry()), AddressCountry.class);
        addressValidator.validate(request.address());
        Person responsiblePerson = personProvider.getPerson(request.responsiblePersonId())
                .orElseThrow(() -> new BusinessException(OrganizationConstants.RESPONSIBLE_PERSON_NOT_FOUND));
        Organization organization = Organization.builder()
                .person(person)
                .responsiblePerson(responsiblePerson)
                .responsibleEmail(request.responsibleEmail())
                .address(request.address())
                .build();
        return OrganizationResponse.from(organizationRepository.save(organization));
    }
}
