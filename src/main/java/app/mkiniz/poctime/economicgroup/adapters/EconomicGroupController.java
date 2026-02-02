package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/economic-group")
@AllArgsConstructor
@Validated
public class EconomicGroupController {

    private final AddBusinessUseCase<EconomicGroupRequest, EconomicGroupResponse> addEconomicGroupService;
    private final UpdateBusinessUseCase<String, EconomicGroupRequest, EconomicGroupResponse> updateEconomicGroupService;

    @PostMapping
    public ResponseEntity<EconomicGroupResponse> createEconomicGroup(@Valid @RequestBody EconomicGroupRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addEconomicGroupService.execute(request));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<EconomicGroupResponse> updateEconomicGroup(@PathVariable String id,
                                                                     @Valid @RequestBody EconomicGroupRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateEconomicGroupService.execute(id, request));
    }
}
