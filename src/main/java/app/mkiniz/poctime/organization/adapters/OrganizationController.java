package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.GetOrganizationFromListUseCase;
import app.mkiniz.poctime.organization.domain.*;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

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
    private final GetAllBusinessUseCase<String, Maybe<Slice<OrganizationProjectionResponse>>> getAllOrganizationProjectionService;
    private final GetOrganizationFromListUseCase getOrganizationFromListUseCase;

    @PostMapping
    public ResponseEntity<OrganizationResponse> createOrganization(@Valid @RequestBody OrganizationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addOrganizationService.execute(request));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<OrganizationResponse> updateOrganization(@PathVariable Tsid id, @Valid @RequestBody UpdateOrganizationRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(updateOrganizationService.execute(id, request));
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
                    @Spec(path = "person.name", params = "name", spec = LikeIgnoreCase.class),
                    @Spec(path = "responsiblePerson.name", params = "respName", spec = LikeIgnoreCase.class),
                    @Spec(path = "responsibleEmail", params = "responsibleEmail", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.street", params = "street", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.city", params = "city", spec = LikeIgnoreCase.class),
                    @Spec(path = "address.stateCode", params = "stateCode", spec = EqualIgnoreCase.class)

            }) Specification<Organization> spec, Pageable pageable) {
        return getAllOrganizationService.execute(pageable, spec)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

    @GetMapping(path = "/projection/all-with-city")
    public ResponseEntity<Slice<OrganizationProjectionResponse>> getAllOrganizationsProjection(
            @RequestParam(name = "documentType", required = false) String docType,
            Pageable pageable) {
        String documentType = Objects.nonNull(docType) ? docType.toLowerCase() : null;
        return getAllOrganizationProjectionService.execute(pageable, documentType)
                .fold(ResponseEntity::ok, () -> ResponseEntity.noContent().build());
    }

    @GetMapping(path = "/projection/from-list")
    public ResponseEntity<Slice<GetOrganizationFromListUseCase.OrganizationListView>> getAllOrganizationsFromListProjection(
            @RequestParam(name = "ids", required = false) List<Tsid> ids) {
        GetOrganizationFromListUseCase.OrganizationListRequest request = new GetOrganizationFromListUseCase.OrganizationListRequest(ids);
        return ResponseEntity.ok(getOrganizationFromListUseCase.execute(request));
    }

}
