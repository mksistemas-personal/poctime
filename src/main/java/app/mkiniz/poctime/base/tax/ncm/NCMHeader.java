package app.mkiniz.poctime.base.tax.ncm;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NCMHeader(LocalDateTime lastUpdate, String version) {

}
