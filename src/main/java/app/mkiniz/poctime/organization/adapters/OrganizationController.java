package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.domain.Organization;
import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.organization.domain.OrganizationResponse;
import app.mkiniz.poctime.organization.domain.UpdateOrganizationRequest;
import app.mkiniz.poctime.shared.business.*;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Maybe;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import net.kaczmarzyk.spring.data.jpa.domain.EqualIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
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
    private final GetAllBusinessUseCase<Specification<Organization>, Maybe<Slice<OrganizationResponse>>> getAllOrganizationService;

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

    @GetMapping
    public ResponseEntity<Slice<OrganizationResponse>> getAllOrganizations(
            @And({
                    @Spec(path = "responsibleEmail", params = "responsibleEmail", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.street", params = "street", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.city", params = "city", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.stateCode", params = "stateCode", spec = EqualIgnoreCase.class)

            }) Specification<Organization> spec, Pageable pageable) {
        return getAllOrganizationService.execute(pageable, spec)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }
}
