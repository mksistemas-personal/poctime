package app.mkiniz.poctime.base.tax.cfop;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CFOPRepository {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private final Map<String, CFOPItem> cfopMap = new HashMap<>();

    @PostConstruct
    public void loadCfops() {
        log.info("Iniciando o carregamento do arquivo cfop.json");
        try {
            Resource resource = resourceLoader.getResource("classpath:lists/cfop.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<CFOPItem> items = objectMapper.readValue(inputStream, new TypeReference<List<CFOPItem>>() {
                });
                for (CFOPItem item : items) {
                    cfopMap.put(item.code(), item);
                }
                log.info("Carregamento de CFOP concluído. Total: {}", cfopMap.size());
            }
        } catch (IOException e) {
            log.error("Erro ao carregar o arquivo cfop.json", e);
            throw new RuntimeException("Falha ao inicializar o CfopRepository", e);
        }
    }

    public Optional<CFOPItem> findByCode(String code) {
        return Optional.ofNullable(cfopMap.get(code));
    }
}
