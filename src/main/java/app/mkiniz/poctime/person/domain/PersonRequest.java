package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.person.PersonConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PersonRequest(
        @NotNull(message = PersonConstants.NAME_NOT_NULL)
        @NotBlank(message = PersonConstants.NAME_NOT_BLANK)
        String name,
        @NotNull(message = PersonConstants.DOCUMENT_NOT_NULL)
        Document<?, ?> document) {
}
