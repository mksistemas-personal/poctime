package app.mkiniz.poctime.organization;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.shared.business.QueryBusinessUseCase;
import com.github.f4b6a3.tsid.Tsid;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface GetOrganizationFromListUseCase extends QueryBusinessUseCase<GetOrganizationFromListUseCase.OrganizationListRequest,
        Slice<GetOrganizationFromListUseCase.OrganizationListView>> {

    interface OrganizationListView {
        String getId();

        String getName();

        Document<?, ?> getDocument();
    }

    record OrganizationListRequest(List<Tsid> ids) {
    }
}
