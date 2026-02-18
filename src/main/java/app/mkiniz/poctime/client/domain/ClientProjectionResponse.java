package app.mkiniz.poctime.client.domain;

public record ClientProjectionResponse(String id, String personId, String personName, String documentType,
                                       String documentNumber, String city) {
}
