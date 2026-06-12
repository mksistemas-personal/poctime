package app.mkiniz.poctime.base.tax.cst;

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
public class CSTRepository {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    private final Map<String, CSTItem> ipiMap = new HashMap<>();
    private final Map<String, CSTItem> pisMap = new HashMap<>();
    private final Map<String, CSTItem> cofinsMap = new HashMap<>();

    @PostConstruct
    public void loadCsv() {
        log.info("Iniciando o carregamento do arquivo cst.json");
        try {
            Resource resource = resourceLoader.getResource("classpath:lists/cst.json");
            try (InputStream inputStream = resource.getInputStream()) {
                List<CSTItem> items = objectMapper.readValue(inputStream, new TypeReference<List<CSTItem>>() {
                });

                for (CSTItem item : items) {
                    if ("ipi".equalsIgnoreCase(item.getType())) {
                        ipiMap.put(item.getCode(), item);
                    } else if ("pis".equalsIgnoreCase(item.getType())) {
                        pisMap.put(item.getCode(), item);
                    } else if ("cofins".equalsIgnoreCase(item.getType())) {
                        cofinsMap.put(item.getCode(), item);
                    }
                }
                log.info("Carregamento concluído. IPI: {}, PIS: {}, COFINS: {}", ipiMap.size(), pisMap.size(), cofinsMap.size());
            }
        } catch (IOException e) {
            log.error("Erro ao carregar o arquivo cst.json", e);
            throw new RuntimeException("Falha ao inicializar o CSVRepository", e);
        }
    }

    public Optional<CSTItem> findIpiByCode(String code) {
        return Optional.ofNullable(ipiMap.get(code));
    }

    public Optional<CSTItem> findPisByCode(String code) {
        return Optional.ofNullable(pisMap.get(code));
    }

    public Optional<CSTItem> findCofinsByCode(String code) {
        return Optional.ofNullable(cofinsMap.get(code));
    }
}
