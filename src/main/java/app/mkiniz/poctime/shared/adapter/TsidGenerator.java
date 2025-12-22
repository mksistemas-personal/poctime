package app.mkiniz.poctime.shared.adapter;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.stereotype.Component;

@Component
public class TsidGenerator {

    private final TsidFactory factory = TsidFactory.newInstance256();

    public long newIdAsLong() {
        return factory.create().toLong();
    }

    public String newIdAsString() {
        return factory.create().toString();
    }

    public Tsid newTsid() {
        return factory.create();
    }

    public static String fromLongToString(Long id) {
        return Tsid.from(id).toLowerCase();
    }

    public static Long fromStringToLong(String id) {
        return Tsid.from(id).toLong();
    }
}
