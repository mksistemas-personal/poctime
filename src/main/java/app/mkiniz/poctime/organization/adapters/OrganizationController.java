package app.mkiniz.poctime.organization.adapters;

import app.mkiniz.poctime.organization.domain.OrganizationRequest;
import app.mkiniz.poctime.person.domain.PersonResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/organization")
@AllArgsConstructor
@Validated
public class OrganizationController {

    @PostMapping
    public PersonResponse createOrganization(@Valid @RequestBody OrganizationRequest request) {
        return null;
    }
}
