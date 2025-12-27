package app.mkiniz.poctime.base.address;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class BrasilianAddressTest {

    private BrazilianAddress executer;
    private Address completAddress;

    @BeforeEach
    void setUp() {
        this.executer = new BrazilianAddress();
        this.completAddress = Address.builder()
                .state("Santa Catarina")
                .city("Lages")
                .stateCode("SC")
                .street("Avenida Belisario Ramos")
                .zipCode("88596-000")
                .complement("Casa")
                .neighborhood("Guadalupe")
                .number("3185")
                .country("BR")
                .build();
    }

    @Test
    void validatePerfectCase() {
        Either<BusinessException, Address> response = executer.validate(completAddress);
        assertTrue(response.isRight());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void validateStreetEmpty(String street) {
        Address address = Address.builder()
                .street(street)
                .country(completAddress.country())
                .zipCode(completAddress.zipCode())
                .number(completAddress.number())
                .city(completAddress.city())
                .state(completAddress.state())
                .stateCode(completAddress.stateCode())
                .neighborhood(completAddress.neighborhood())
                .complement(completAddress.complement())
                .build();
        validateField(address, AddressCountry.STREET_REQUIRED);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void validateCountryEmpty(String country) {
        Address address = Address.builder()
                .street(completAddress.street())
                .country(country)
                .zipCode(completAddress.zipCode())
                .number(completAddress.number())
                .city(completAddress.city())
                .state(completAddress.state())
                .stateCode(completAddress.stateCode())
                .neighborhood(completAddress.neighborhood())
                .complement(completAddress.complement())
                .build();
        validateField(address, AddressCountry.COUNTRY_REQUIRED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"BC", "bc", "Bc"})
    void validateCountryInvalid(String country) {
        Address address = Address.builder()
                .street(completAddress.street())
                .country(country)
                .zipCode(completAddress.zipCode())
                .number(completAddress.number())
                .city(completAddress.city())
                .state(completAddress.state())
                .stateCode(completAddress.stateCode())
                .neighborhood(completAddress.neighborhood())
                .complement(completAddress.complement())
                .build();
        validateField(address, AddressCountry.COUNTRY_INVALID);
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    void validateZipCodeEmpty(String zipCode) {
        Address address = Address.builder()
                .street(completAddress.street())
                .country(completAddress.country())
                .zipCode(zipCode)
                .number(completAddress.number())
                .city(completAddress.city())
                .state(completAddress.state())
                .stateCode(completAddress.stateCode())
                .neighborhood(completAddress.neighborhood())
                .complement(completAddress.complement())
                .build();
        validateField(address, AddressCountry.ZIP_CODE_REQUIRED);
    }

    @ParameterizedTest
    @ValueSource(strings = {"88506000", "88506-A00", "A88506-000"})
    void validateZipCodeInvalid(String zipCode) {
        Address address = Address.builder()
                .street(completAddress.street())
                .country(completAddress.country())
                .zipCode(zipCode)
                .number(completAddress.number())
                .city(completAddress.city())
                .state(completAddress.state())
                .stateCode(completAddress.stateCode())
                .neighborhood(completAddress.neighborhood())
                .complement(completAddress.complement())
                .build();
        validateField(address, AddressCountry.ZIP_CODE_INVALID);
    }

    private void validateField(Address address, String message) {
        Either<BusinessException, Address> response = executer.validate(address);
        response.fold(
                error -> {
                    assertEquals(message, error.getMessage());
                    return error; // ou return error
                },
                success -> {
                    fail("Expected BusinessException, but got success:");
                    return null;
                }
        );
    }

    @Test
    void canonicalize() {
    }
}