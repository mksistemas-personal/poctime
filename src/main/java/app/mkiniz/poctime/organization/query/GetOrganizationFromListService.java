package app.mkiniz.poctime.organization.query;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.base.document.DocumentConverter;
import app.mkiniz.poctime.organization.GetOrganizationFromListUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetOrganizationFromListService implements GetOrganizationFromListUseCase {

    private static final String selectQuery = """
            select
                o.id,
                p.name,
                p.document
            from
                organization o inner join person p on o.person_id = p.id
            where
                o.deleted = false AND
                (CAST(:ids AS bigint[]) is null OR o.id = ANY(CAST(:ids AS bigint[])))
            """;
    private final EntityManager entityManager;
    private final ObjectMapper mapper;

    @SuppressWarnings("unchecked")
    @Override
    public Slice<OrganizationListView> execute(OrganizationListRequest organizationListRequest) {
        Long[] ids = null;
        if (!Objects.isNull(organizationListRequest.ids()))
            ids = organizationListRequest.ids().stream().map(Tsid::toLong).toArray(Long[]::new);
        NativeQuery<OrganizationListView> query = entityManager.createNativeQuery(selectQuery)
                .unwrap(NativeQuery.class)
                .setParameter("ids", ids, Long[].class)
                .setTupleTransformer(OrganizationListViewImpl::new);
        List<OrganizationListView> result = query.getResultList();
        return new SliceImpl<>(result);
    }

    private static class OrganizationListViewImpl implements OrganizationListView {

        private final String id;
        private final String name;
        private final Document<?, ?> document;

        public OrganizationListViewImpl(Object[] tuples, String[] alias) {
            id = Tsid.from((Long) tuples[0]).toLowerCase();
            name = (String) tuples[1];
            DocumentConverter converter = new DocumentConverter();
            document = converter.convertToEntityAttribute((String) tuples[2]);
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Document<?, ?> getDocument() {
            return document;
        }
    }

}
