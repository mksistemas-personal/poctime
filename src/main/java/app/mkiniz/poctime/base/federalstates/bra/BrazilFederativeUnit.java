package app.mkiniz.poctime.base.federalstates.bra;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum BrazilFederativeUnit {
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

    BrazilFederativeUnit(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Retorna uma lista com todas as UFs (objetos Enum)
     */
    public static List<BrazilFederativeUnit> getAll() {
        return Arrays.asList(BrazilFederativeUnit.values());
    }

    /**
     * Retorna apenas as siglas como String
     */
    public static List<String> getAllCodes() {
        return Arrays.stream(BrazilFederativeUnit.values())
                .map(BrazilFederativeUnit::name)
                .collect(Collectors.toList());
    }

    public static Optional<BrazilFederativeUnit> fromStateCode(String stateCode) {
        try {
            return Optional.of(BrazilFederativeUnit.valueOf(stateCode.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static Optional<BrazilFederativeUnit> fromState(String state) {
        return Arrays.stream(BrazilFederativeUnit.values())
                .filter(f -> f.name.equalsIgnoreCase(state))
                .findFirst();
    }
}
