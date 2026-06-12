package app.mkiniz.poctime.shared.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper redisObjectMapper;

    public void save(Tsid tenantId, String category, String code, Object value) {
        String key = buildKey(tenantId, category, code);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Recupera um objeto do Redis pela chave composta sem conversão de classe.
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(Tsid tenantId, String category, String code, Class<T> targetType) {
        String key = buildKey(tenantId, category, code);
        Object value = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }
        return Optional.of(redisObjectMapper.convertValue(value, targetType));
    }

    public List<Object> findByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (Objects.isNull(keys) || keys.isEmpty()) {
            return List.of();
        }
        return redisTemplate.opsForValue().multiGet(keys).stream()
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Object> findAllByTenant(Tsid tenantId) {
        return findByPattern(tenantId.toLowerCase() + ":*:*");
    }

    public List<Object> findAllByTenantAndCategory(Tsid tenantId, String category) {
        return findByPattern(tenantId.toLowerCase() + ":" + category + ":*");
    }

    public void deleteAllByCategory(Tsid tenantId, String category) {
        String pattern = tenantId.toLowerCase() + ":" + category + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (Objects.nonNull(keys) && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void saveAll(Tsid tenantId, String category, Map<String, Object> items) {
        Map<String, Object> values = items.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> buildKey(tenantId, category, entry.getKey()),
                        Map.Entry::getValue
                ));
        redisTemplate.opsForValue().multiSet(values);
    }

    private String buildKey(Tsid tenantId, String category, String code) {
        return String.format("%s:%s:%s", tenantId.toLowerCase(), category, code);
    }
}
