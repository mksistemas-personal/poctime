package app.mkiniz.poctime.shared.adapter;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;

@Getter
@Builder
public class StandardError {
    private Instant timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;
}
