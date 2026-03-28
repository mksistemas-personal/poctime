package app.mkiniz.poctime.shared.repository;

import com.github.f4b6a3.tsid.Tsid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Armazena um objeto no Redis.
     * Chave composta: tenantId:category:code
     */
    public void save(Tsid tenantId, String category, String code, Object value) {
        String key = buildKey(tenantId, category, code);
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Recupera um objeto do Redis pela chave composta.
     */
    public Object get(Tsid tenantId, String category, String code) {
        String key = buildKey(tenantId, category, code);
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Recupera uma lista de objetos baseada em um padrão de chave.
     * Pode ser usado para buscar por dimensões:
     * - Por tenant: tenantId:*
     * - Por tenant e categoria: tenantId:category:*
     */
    public List<Object> findByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return List.of();
        }
        return redisTemplate.opsForValue().multiGet(keys).stream()
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Busca todos os objetos de um tenant.
     */
    public List<Object> findAllByTenant(Tsid tenantId) {
        return findByPattern(tenantId.toLowerCase() + ":*:*");
    }

    /**
     * Busca todos os objetos de uma categoria para um determinado tenant.
     */
    public List<Object> findAllByTenantAndCategory(Tsid tenantId, String category) {
        return findByPattern(tenantId.toLowerCase() + ":" + category + ":*");
    }

    /**
     * Remove todas as chaves de uma categoria para um determinado tenant.
     */
    public void deleteAllByCategory(Tsid tenantId, String category) {
        String pattern = tenantId.toLowerCase() + ":" + category + ":*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * Armazena uma lista de objetos no Redis.
     *
     * @param items Mapa onde a chave é o código do item e o valor é o objeto.
     */
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

    @Configuration
    public static class RedisConfig {
        @Bean
        public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
            RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            template.setKeySerializer(new StringRedisSerializer());
            template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());
            template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
            template.setEnableTransactionSupport(true);
            return template;
        }
    }
}
