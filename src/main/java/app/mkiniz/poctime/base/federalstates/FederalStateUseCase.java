package app.mkiniz.poctime.base.federalstates;

import java.util.List;

public interface FederalStateUseCase {

    List<FederalStateResponse> findAll();

    public record FederalStateResponse(String stateName, String stateCode) {
    }

    public static final String DEFAULT_COUNTRY = "br";

    public static String getBeanFromCountry(String country) {
        return "state-" + country.toLowerCase();
    }

}
