package app.mkiniz.poctime.person.domain;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import lombok.Builder;

@Builder
public record PersonResponse(String id, String name, Document<?, ?> document) {
    public static PersonResponse fromPerson(Person person) {
        return PersonResponse.builder()
                .id(TsidGenerator.fromLongToString(person.getId()))
                .name(person.getName())
                .document(person.getDocument())
                .build();
    }
}
