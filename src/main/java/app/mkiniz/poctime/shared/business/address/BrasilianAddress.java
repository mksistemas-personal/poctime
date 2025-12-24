package app.mkiniz.poctime.shared.business.address;

import app.mkiniz.poctime.base.CountryConstant;
import app.mkiniz.poctime.shared.business.base.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component("address-br")
class BrasilianAddress implements AddressCountry {

    private static final String zipCodeRegex = "^[0-9]{5}-[0-9]{3}$";

    @Override
    public void validate(Address address) {
        if (!StringUtils.hasText(address.street()))
            throw new BusinessException(AddressCountry.STREET_REQUIRED);
        if (!StringUtils.hasText(address.neighborhood()))
            throw new BusinessException(AddressCountry.NEIGHBORHOOD_REQUIRED);
        if (!StringUtils.hasText(address.city()))
            throw new BusinessException(AddressCountry.CITY_REQUIRED);
        if (!StringUtils.hasText(address.stateCode()) && !StringUtils.hasText(address.state()))
            throw new BusinessException(AddressCountry.STATE_CODE_REQUIRED);
        if (StringUtils.hasText(address.stateCode())) {
            Optional<BrasilFederativeUnit> federalUnit = BrasilFederativeUnit.fromStateCode(address.stateCode());
            if (federalUnit.isEmpty())
                throw new BusinessException(AddressCountry.STATE_CODE_INVALID);
            if (StringUtils.hasText(address.state()) &&
                    !federalUnit.get().name().equalsIgnoreCase(address.state()))
                throw new BusinessException(AddressCountry.STATE_INVALID);
        } else if (StringUtils.hasText(address.state()) &&
                BrasilFederativeUnit.fromState(address.state()).isEmpty())
            throw new BusinessException(AddressCountry.STATE_INVALID);
        if (!StringUtils.hasText(address.country()))
            throw new BusinessException(AddressCountry.COUNTRY_REQUIRED);
        if (!CountryConstant.BR.equals(address.country()))
            throw new BusinessException(AddressCountry.COUNTRY_INVALID);
        if (!StringUtils.hasText(address.zipCode()))
            throw new BusinessException(AddressCountry.ZIP_CODE_REQUIRED);
        if (!address.zipCode().matches(zipCodeRegex))
            throw new BusinessException(AddressCountry.ZIP_CODE_INVALID);
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
