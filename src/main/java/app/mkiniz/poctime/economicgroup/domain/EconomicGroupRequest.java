package app.mkiniz.poctime.economicgroup.domain;

import app.mkiniz.poctime.economicgroup.EconomicGroupConstants;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record EconomicGroupRequest(
        Tsid id,

        @NotBlank(message = EconomicGroupConstants.NAME_NOT_BLANK)
        String name,

        String description,

        List<String> organizationIds) {
}
