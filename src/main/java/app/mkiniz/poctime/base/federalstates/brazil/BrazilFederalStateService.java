package app.mkiniz.poctime.base.federalstates.brazil;

import app.mkiniz.poctime.base.federalstates.FederalStateUseCase;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component("state-br")
class BrazilFederalStateService implements FederalStateUseCase {
    @Override
    public List<FederalStateResponse> findAll() {

        return Arrays.stream(BrazilFederativeUnit.values())
                .map(uf ->
                        new FederalStateResponse(uf.getName(), uf.name()))
                .collect(Collectors.toList());
    }
}
