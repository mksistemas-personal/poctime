package app.mkiniz.poctime.organization.domain;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.PersonConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

public record PersonRequest(
        String id,
        @NotNull(message = PersonConstants.NAME_NOT_NULL)
        @NotBlank(message = PersonConstants.NAME_NOT_BLANK)
        String name,
        @NotNull(message = PersonConstants.DOCUMENT_NOT_NULL)
        Document<?, ?> document) {

    public boolean isNew() {
        return StringUtils.isBlank(id);
    }

}
