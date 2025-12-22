package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import lombok.Builder;

@Builder
public record PersonDeletedEvent(String id, String name, Document<?, ?> document) {
}
