package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import lombok.Builder;

@Builder
public record OrganizationProjection(
        String id,
        String personId,
        String personName,
        String documentType,
        String documentNumber,
        String city,
        String responsibleId,
        String responsibleName,
        String responsibleEmail,
        String responsibleDocumentType,
        String responsibleDocumentNumber
) {
    public static OrganizationProjection from(Organization organization) {
        return OrganizationProjection.builder()
                .id(TsidGenerator.fromLongToString(organization.getId()))
                .personId(TsidGenerator.fromLongToString(organization.getPerson().getId()))
                .personName(organization.getPerson().getName())
                .documentType(organization.getPerson().getDocument().getType())
                .documentNumber(organization.getPerson().getDocument().identifier().toString())
                .city(organization.getAddress().city())
                .responsibleId(TsidGenerator.fromLongToString(organization.getResponsiblePerson().getId()))
                .responsibleName(organization.getResponsiblePerson().getName())
                .responsibleEmail(organization.getResponsibleEmail())
                .responsibleDocumentType(organization.getResponsiblePerson().getDocument().getType())
                .responsibleDocumentNumber(organization.getResponsiblePerson().getDocument().identifier().toString())
                .build();
    }
}
