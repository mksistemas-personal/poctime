package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import lombok.Builder;

@Builder
public record PersonAddedEvent(String id, String name, Document<?, ?> document) {
}
