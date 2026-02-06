package app.mkiniz.poctime.economicgroup;

import app.mkiniz.poctime.shared.business.BusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface RemoveOrganizationUseCase extends
        BusinessUseCase<
                RemoveOrganizationUseCase.RemoveOrganizationRequest,
                RemoveOrganizationUseCase.RemoveOrganizationResponse> {

    RemoveOrganizationResponse execute(RemoveOrganizationRequest request);

    record RemoveOrganizationRequest(
            @NotNull(message = EconomicGroupConstants.REMOVE_ORGANIZATION_IDS_NOT_NULL)
            @NotEmpty(message = EconomicGroupConstants.REMOVE_ORGANIZATION_IDS_NOT_EMPTY)
            List<Tsid> organizationIds) {
    }

    record RemoveOrganizationResponse(List<Tsid> economicGroupIds) {
    }

}
