package app.mkiniz.poctime.base.tax.ncm;

import app.mkiniz.poctime.shared.repository.RedisRepository;
import com.github.f4b6a3.tsid.Tsid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
class FillNCMServiceImpl implements FillNCMService {

    private final RedisRepository repository;
    private final RestTemplate restTemplate;

    @Value(NCMConstants.NCM_URL_VALUE)
    private String ncmUrl;

    @Value(NCMConstants.NCM_TENANT_VALUE)
    private String tenant;

    @Override
    public void execute() {
        ResponseEntity<List<NCMItem>> response = restTemplate.exchange(
                ncmUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<NCMItem>>() {
                }
        );

        List<NCMItem> items = response.getBody();
        if (items == null || items.isEmpty()) {
            return;
        }

        Tsid tenantCode = Tsid.from(tenant);
        repository.deleteAllByCategory(tenantCode, NCMConstants.NCM_CATEGORY);

        Map<String, Object> itemsMap = items.stream()
                .collect(Collectors.toMap(NCMItem::code, item -> item));
        repository.saveAll(tenantCode, NCMConstants.NCM_CATEGORY, itemsMap);
    }
}
