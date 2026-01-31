package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/economic-group")
@AllArgsConstructor
@Validated
public class EconomicGroupController {

    private final AddBusinessUseCase<EconomicGroupRequest, EconomicGroupResponse> addEconomicGroupService;

    @PostMapping
    public ResponseEntity<EconomicGroupResponse> createOrganization(@Valid @RequestBody EconomicGroupRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addEconomicGroupService.execute(request));
    }
}
