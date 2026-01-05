package app.mkiniz.poctime.organization;

import com.github.f4b6a3.tsid.Tsid;

public interface OrganizationProvider {
    boolean canRemovePerson(Tsid personId);
}
