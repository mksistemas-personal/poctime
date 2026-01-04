package app.mkiniz.poctime.base.address;

import app.mkiniz.poctime.base.CountryConstant;
import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component("address-br")
public class BrazilianAddress implements AddressCountry {

    private static final String zipCodeRegex = "^[0-9]{5}-[0-9]{3}$";

    @Override
    public Either<BusinessException, Address> validate(Address addressInput) {
        return Either.<BusinessException, Address>right(addressInput)
                .flatMap(address -> StringUtils.hasText(address.street()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(STREET_REQUIRED)))
                .flatMap(address -> StringUtils.hasText(address.neighborhood()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(NEIGHBORHOOD_REQUIRED)))
                .flatMap(address -> StringUtils.hasText(address.city()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(CITY_REQUIRED)))
                .flatMap(address -> StringUtils.hasText(address.stateCode()) || StringUtils.hasText(address.state()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(STATE_CODE_REQUIRED)))
                .flatMap(this::validateStateCode)
                .flatMap(address -> StringUtils.hasText(address.state()) &&
                        BrasilFederativeUnit.fromState(address.state()).isEmpty() ?
                        Either.left(new BusinessException(STATE_INVALID)) :
                        Either.right(address))
                .flatMap(address -> StringUtils.hasText(address.country()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(COUNTRY_REQUIRED)))
                .flatMap(address -> CountryConstant.BR.equalsIgnoreCase(address.country()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(COUNTRY_INVALID)))
                .flatMap(address -> StringUtils.hasText(address.zipCode()) ?
                        Either.right(address) :
                        Either.left(new BusinessException(ZIP_CODE_REQUIRED)))
                .flatMap(address -> address.zipCode().matches(zipCodeRegex) ?
                        Either.right(address) :
                        Either.left(new BusinessException(ZIP_CODE_INVALID)));
    }

    private Either<BusinessException, Address> validateStateCode(Address address) {
        if (StringUtils.hasText(address.stateCode())) {
            Optional<BrasilFederativeUnit> federalUnit = BrasilFederativeUnit.fromStateCode(address.stateCode());
            if (federalUnit.isEmpty())
                return Either.left(new BusinessException(AddressCountry.STATE_CODE_INVALID));
            if (StringUtils.hasText(address.state()) &&
                    !federalUnit.get().getName().equalsIgnoreCase(address.state()))
                return Either.left(new BusinessException(AddressCountry.STATE_INVALID));
        }
        return Either.right(address);
    }

    @Override
    public Address canonicalize(Address address) {
        return Address.builder()
                .city(StringUtils.capitalize(address.city()))
                .state(StringUtils.capitalize(address.state()))
                .neighborhood(StringUtils.capitalize(address.neighborhood()))
                .complement(StringUtils.capitalize(address.complement()))
                .number(address.number())
                .zipCode(address.zipCode())
                .street(StringUtils.capitalize(address.street()))
                .country(address.country().toUpperCase())
                .stateCode(address.stateCode().toUpperCase())
                .build();
    }
}
