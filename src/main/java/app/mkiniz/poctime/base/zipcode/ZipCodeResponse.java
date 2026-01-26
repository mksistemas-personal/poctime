package app.mkiniz.poctime.base.zipcode;

public record ZipCodeResponse(String zipCode,
                              String state,
                              String city,
                              String neighborhood,
                              String street,
                              String service) {
}
