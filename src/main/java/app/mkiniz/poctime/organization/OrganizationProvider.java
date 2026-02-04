package app.mkiniz.poctime.organization;

import com.github.f4b6a3.tsid.Tsid;

import java.util.List;

public interface OrganizationProvider {
    boolean canRemovePerson(Tsid personId);

    List<String> getOrganizationsNotFound(List<String> ids);
}
