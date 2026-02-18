package app.mkiniz.poctime.client;

import com.github.f4b6a3.tsid.Tsid;

public interface ClientProvider {
    Long count();

    boolean canRemovePerson(Tsid personId);
}
