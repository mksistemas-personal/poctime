package app.mkiniz.poctime.organization.query;

import app.mkiniz.poctime.base.document.Document;
import app.mkiniz.poctime.organization.GetOrganizationFromListUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
class GetOrganizationFromListService implements GetOrganizationFromListUseCase {

    private final JdbcClient jdbcClient;
    private final ObjectMapper mapper;

    @Override
    public Slice<OrganizationListView> execute(OrganizationListRequest organizationListRequest) {
        StringBuilder sql = new StringBuilder("""
                SELECT
                    o.id,
                    p.name,
                    p.document::text as document_json
                FROM
                    organization o INNER JOIN person p ON o.person_id = p.id
                WHERE
                    o.deleted = false
                """);

        Map<String, Object> params = new HashMap<>();
        if (organizationListRequest.ids() != null && !organizationListRequest.ids().isEmpty()) {
            sql.append(" AND o.id IN (:ids)");
            params.put("ids", organizationListRequest.ids().stream().map(Tsid::toLong).toList());
        }

        List<OrganizationListView> result = jdbcClient.sql(sql.toString())
                .params(params)
                .query((rs, rowNum) -> {
                    long id = rs.getLong("id");
                    String name = rs.getString("name");
                    String documentJson = rs.getString("document_json");
                    Document<?, ?> document = null;
                    try {
                        if (documentJson != null) {
                            document = mapper.readValue(documentJson, Document.class);
                        }
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    return (OrganizationListView) new OrganizationListViewDto(Tsid.from(id).toLowerCase(), name, document);
                })
                .list();

        return new SliceImpl<>(result);
    }

    private record OrganizationListViewDto(String id, String name,
                                           Document<?, ?> document) implements OrganizationListView {
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
