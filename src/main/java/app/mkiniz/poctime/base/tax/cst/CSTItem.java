package app.mkiniz.poctime.base.tax.cst;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSTItem {
    private String type;
    private String code;
    private String description;
}
