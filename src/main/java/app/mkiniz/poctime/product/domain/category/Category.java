package app.mkiniz.poctime.product.domain.category;

import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private Long id;

    private String name;

    private boolean deleted = false;
}
