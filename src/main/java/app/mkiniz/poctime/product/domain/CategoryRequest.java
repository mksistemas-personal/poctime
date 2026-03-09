package app.mkiniz.poctime.product.domain;

import com.github.f4b6a3.tsid.Tsid;

public record CategoryRequest(
        Tsid id,
        String name) {
}
