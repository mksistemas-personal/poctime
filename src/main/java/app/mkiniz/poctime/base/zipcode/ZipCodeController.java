package app.mkiniz.poctime.base.zipcode;

import app.mkiniz.poctime.base.zipcode.brasil.CepResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ceps")
public class ZipCodeController {

    private final ZipCodeUseCase zipCodeService;

    public ZipCodeController(ZipCodeUseCase zipCodeService) {
        this.zipCodeService = zipCodeService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<CepResponse> getCep(@PathVariable String cep) {
        return zipCodeService.findByCep(cep)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
