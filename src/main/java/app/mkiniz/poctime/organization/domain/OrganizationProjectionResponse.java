package app.mkiniz.poctime.organization.domain;

public record OrganizationProjectionResponse(String id, String personId, String personName, String documentType,
                                             String documentNumber, String city) {
}
