package app.mkiniz.poctime.economicgroup.adapters;

import app.mkiniz.poctime.economicgroup.domain.EconomicGroupRequest;
import app.mkiniz.poctime.economicgroup.domain.EconomicGroupResponse;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
    private final UpdateBusinessUseCase<Tsid, EconomicGroupRequest, EconomicGroupResponse> updateEconomicGroupService;
    private final DeleteBusinessUseCase<Tsid, EconomicGroupResponse> deleteEconomicGroupService;
    private final GetByIdBusinessUseCase<Tsid, EconomicGroupResponse> getIdEconomicGroupService;
    private final GetAllBusinessUseCase<String, Maybe<Slice<EconomicGroupResponse>>> getAllEconomicGroupService;

    @PostMapping
    public ResponseEntity<EconomicGroupResponse> createEconomicGroup(@Valid @RequestBody EconomicGroupRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(addEconomicGroupService.execute(request));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<EconomicGroupResponse> updateEconomicGroup(@PathVariable Tsid id,
                                                                     @Valid @RequestBody EconomicGroupRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateEconomicGroupService.execute(id, request));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<EconomicGroupResponse> deleteEconomicGroup(@PathVariable Tsid id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(deleteEconomicGroupService.execute(id));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EconomicGroupResponse> getIdEconomicGroup(@PathVariable Tsid id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(getIdEconomicGroupService.execute(id));
    }

    @GetMapping
    public ResponseEntity<Slice<EconomicGroupResponse>> getAllEconomicGroup(@RequestParam(required = false) String term, Pageable pageable) {
        return getAllEconomicGroupService.execute(pageable, term)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

}
