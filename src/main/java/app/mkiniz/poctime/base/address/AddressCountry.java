package app.mkiniz.poctime.base.address;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;

public interface AddressCountry {

    String ADDRESS = "address-";

    Either<BusinessException, Address> validate(Address address);

    Address canonicalize(Address address);

    static String getCountry(String countryCode) {
        return (ADDRESS + countryCode).toLowerCase();
    }

    static final String STREET_REQUIRED = "address.street.required";
    static final String NEIGHBORHOOD_REQUIRED = "address.neighborhood.required";
    static final String CITY_REQUIRED = "address.city.required";
    static final String STATE_CODE_REQUIRED = "address.city.required";
    static final String STATE_CODE_INVALID = "address.statecode.invalid";
    static final String STATE_INVALID = "address.state.invalid";
    static final String COUNTRY_REQUIRED = "address.country.required";
    static final String COUNTRY_INVALID = "address.country.invalid";
    static final String ZIP_CODE_REQUIRED = "address.zipcode.required";
    static final String ZIP_CODE_INVALID = "address.zipcode.invalid";
}
