package app.mkiniz.poctime.base.address;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum BrasilFederativeUnit {
    AC("Acre"),
    AL("Alagoas"),
    AP("Amapa"),
    AM("Amazonas"),
    BA("Bahia"),
    CE("Ceara"),
    DF("Distrito Federal"),
    ES("Espirito Santo"),
    GO("Goias"),
    MA("Maranhão"),
    MT("Mato Grosso"),
    MS("Mato Grosso do Sul"),
    MG("Minas Gerais"),
    PA("Para"),
    PB("Paraiba"),
    PR("Parana"),
    PE("Pernambuco"),
    PI("Piaui"),
    RJ("Rio de Janeiro"),
    RN("Rio Grande do Norte"),
    RS("Rio Grande do Sul"),
    RO("Rondonia"),
    RR("Roraima"),
    SC("Santa Catarina"),
    SP("Sao Paulo"),
    SE("Sergipe"),
    TO("Tocantins");

    private final String name;

    BrasilFederativeUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Retorna uma lista com todas as UFs (objetos Enum)
     */
    public static List<BrasilFederativeUnit> getAll() {
        return Arrays.asList(BrasilFederativeUnit.values());
    }

    /**
     * Retorna apenas as siglas como String
     */
    public static List<String> getAllCodes() {
        return Arrays.stream(BrasilFederativeUnit.values())
                .map(BrasilFederativeUnit::name)
                .collect(Collectors.toList());
    }

    public static Optional<BrasilFederativeUnit> fromStateCode(String stateCode) {
        try {
            return Optional.of(BrasilFederativeUnit.valueOf(stateCode.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<BrasilFederativeUnit> fromState(String state) {
        return Arrays.stream(BrasilFederativeUnit.values())
                .filter(f -> f.name.equalsIgnoreCase(state))
                .findFirst();
    }
}
