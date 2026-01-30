package app.mkiniz.poctime.organization;

import java.util.List;

public interface GetOrganizationsNoFoundInListUseCase {
    List<String> execute(List<String> ids);
}
