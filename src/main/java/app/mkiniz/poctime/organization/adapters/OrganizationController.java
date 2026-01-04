package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.organization.domain.UpdateOrganizationRequest;
import app.mkiniz.poctime.shared.business.AddBusinessUseCase;
import app.mkiniz.poctime.shared.business.DeleteBusinessUseCase;
import app.mkiniz.poctime.shared.business.GetByIdBusinessUseCase;
import app.mkiniz.poctime.shared.business.UpdateBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/organization")
@AllArgsConstructor
@Validated
public class OrganizationController {

    private final AddBusinessUseCase<OrganizationRequest, OrganizationResponse> addOrganizationService;
    private final UpdateBusinessUseCase<Tsid, UpdateOrganizationRequest, OrganizationResponse> updateOrganizationService;
    private final DeleteBusinessUseCase<Tsid, OrganizationResponse> deleteOrganizationService;
    private final GetByIdBusinessUseCase<Tsid, OrganizationResponse> getOrganizationByIdService;

    @PostMapping
    public OrganizationResponse createOrganization(@Valid @RequestBody OrganizationRequest request) {
        return addOrganizationService.execute(request);
    }

    @PutMapping(path = "/{id}")
    public OrganizationResponse updateOrganization(@PathVariable Tsid id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return updateOrganizationService.execute(id, request);
    }

    @DeleteMapping(path = "/{id}")
    public OrganizationResponse deleteOrganization(@PathVariable Tsid id) {
        return deleteOrganizationService.execute(id);
    }

    @GetMapping(path = "/{id}")
    public OrganizationResponse getOrganizationById(@PathVariable Tsid id) {
        return getOrganizationByIdService.execute(id);
    }
}
