package app.mkiniz.poctime.base.zipcode;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zipCodes")
public class ZipCodeController {
    private final BeanFactory beanFactory;

    public ZipCodeController(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @GetMapping("/{zipCode}")
    public ResponseEntity<ZipCodeResponse> getZipCode(
            @PathVariable String zipCode,
            @RequestParam(required = false, defaultValue = ZipCodeUseCase.DEFAULT_COUNTRY) String country) {
        ZipCodeUseCase zipCodeService = beanFactory.getBean(ZipCodeUseCase.getBeanFromCountry(country), ZipCodeUseCase.class);
        return zipCodeService.findByCep(zipCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
