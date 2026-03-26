package app.mkiniz.poctime.product.domain.taxdata;

import app.mkiniz.poctime.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "product_tax_data")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTaxData {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_tax_data_product"))
    private Product product;

    @Column(name = "ncm", length = 8)
    private String ncm;

    @Column(name = "cest", length = 7)
    private String cest;

    @Column(name = "cfop", length = 4)
    private String cfop;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin")
    private GoodsOrigin origin;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;
}
