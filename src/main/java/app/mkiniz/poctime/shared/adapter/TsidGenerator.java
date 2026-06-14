package app.mkiniz.poctime.shared.adapter;

import com.github.f4b6a3.tsid.Tsid;

public interface TsidGenerator {
    static String fromLongToString(Long id) {
        return Tsid.from(id).toLowerCase();
    }

    static Long fromStringToLong(String id) {
        return Tsid.from(id).toLong();
    }

    long newIdAsLong();

    String newIdAsString();

    Tsid newTsid();
}
