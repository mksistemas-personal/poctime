package app.mkiniz.poctime.base.federalstates;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/federalStates")
public class FederalStateController {
    private final BeanFactory beanFactory;

    public FederalStateController(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @GetMapping
    public ResponseEntity<List<FederalStateUseCase.FederalStateResponse>> getFederalStates(
            @RequestParam(required = false, defaultValue = FederalStateUseCase.DEFAULT_COUNTRY) String country) {
        FederalStateUseCase service = beanFactory.getBean(FederalStateUseCase.getBeanFromCountry(country), FederalStateUseCase.class);
        return ResponseEntity.ok(service.findAll());
    }
}
