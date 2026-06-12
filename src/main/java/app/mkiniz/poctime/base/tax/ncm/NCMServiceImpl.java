package app.mkiniz.poctime.base.tax.ncm;

import app.mkiniz.poctime.shared.business.BusinessException;
import app.mkiniz.poctime.shared.repository.RedisRepository;
import com.github.f4b6a3.tsid.Tsid;
import cyclops.control.Either;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class NCMServiceImpl implements NCMService {

    private final RedisRepository repository;
    private final RestTemplate restTemplate;

    @Value(NCMConstants.NCM_URL_VALUE)
    private String ncmUrl;

    @Value(NCMConstants.NCM_TENANT_VALUE)
    private String tenant;

    @Value(NCMConstants.NCM_TIMEOUT_IN_SECONDS)
    private int timeoutInSeconds;

    @Override
    @Scheduled(fixedRate = 300000, initialDelay = 10000) // 5 minutos = 300.000 ms
    public void fillRepository() {
        Either.<BusinessException, Context>right(new Context(Tsid.from(tenant)))
                .flatMap(this::retrieveHeader)
                .flatMap(this::verifyHeader)
                .flatMap(this::loadNCMFromApi)
                .flatMap(this::deleteOldNCMItems)
                .flatMap(this::saveNCMItems)
                .map(context ->
                {
                    log.debug("NCM filled successfully for tenant: {}", context.tenantCode);
                    return context;
                })
                .fold(exception ->
                {
                    log.debug("Error filling NCM: {}", exception.getMessage());
                    return exception;
                }, context -> context);
    }

    @Override
    public Optional<NCMItem> findByCode(String code) {
        return repository.get(Tsid.from(tenant), NCMConstants.NCM_CATEGORY, code, NCMItem.class);
    }

    private Either<BusinessException, Context> saveNCMItems(Context context) {
        Map<String, Object> itemsMap = context.items.stream()
                .collect(Collectors.toMap(NCMItem::code, item -> item));
        repository.saveAll(context.tenantCode, NCMConstants.NCM_CATEGORY, itemsMap);
        repository.save(context.tenantCode, NCMConstants.NCM_CATEGORY, NCMConstants.NCM_HEADER, new NCMHeader(LocalDateTime.now(), NCMConstants.NCM_VERSION));
        return Either.right(context);
    }

    private Either<BusinessException, Context> deleteOldNCMItems(Context context) {
        repository.deleteAllByCategory(context.tenantCode, NCMConstants.NCM_CATEGORY);
        return Either.right(context);
    }

    private Either<BusinessException, Context> loadNCMFromApi(Context context) {
        ResponseEntity<NCMApiHeader> response = restTemplate.exchange(
                ncmUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<NCMApiHeader>() {
                }
        );
        if (response.getStatusCode().is2xxSuccessful()) {
            context.items = response.getBody().items();
            if (Objects.isNull(context.items) || context.items.isEmpty()) {
                log.error("NCM API returned empty or null items list");
                return Either.left(new BusinessException(NCMConstants.NCM_API_EMPTY_RESPONSE));
            }
            return Either.right(context);
        } else {
            log.error("NCM API error: {} - {}", response.getStatusCode(), response.getBody());
            return Either.left(new BusinessException(NCMConstants.NCM_API_ERROR));
        }
    }

    private Either<BusinessException, Context> verifyHeader(Context context) {
        LocalDateTime nextDateTime = context.ncmHeader.lastUpdate().plusSeconds(timeoutInSeconds);
        return LocalDateTime.now().isAfter(nextDateTime) ?
                Either.right(context) :
                Either.left(new BusinessException(NCMConstants.NCM_TIMEOUT_NOT_REACHED));
    }

    private Either<BusinessException, Context> retrieveHeader(Context context) {
        context.ncmHeader = repository.<NCMHeader>get(context.tenantCode, NCMConstants.NCM_CATEGORY, NCMConstants.NCM_HEADER, NCMHeader.class)
                .orElseGet(() ->
                        new NCMHeader(
                                LocalDateTime.now().minusSeconds(timeoutInSeconds),
                                NCMConstants.NCM_VERSION));
        return Either.right(context);
    }

    private static class Context {

        public Tsid tenantCode;
        public NCMHeader ncmHeader;
        public List<NCMItem> items;

        public Context(Tsid tenantCode) {
            this.tenantCode = tenantCode;
        }
    }
}
