package app.mkiniz.poctime.base.tax.cst;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CSVRepositoryTest {

    private CSVRepository csvRepository;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        csvRepository = new CSVRepository(objectMapper, resourceLoader);
        csvRepository.loadCsv();
    }

    @Test
    void shouldLoadIpiItems() {
        Optional<CSTItem> item = csvRepository.findIpiByCode("00");
        assertTrue(item.isPresent());
        assertEquals("ipi", item.get().getType());
        assertEquals("00", item.get().getCode());
        assertEquals("Entrada com Recuperação de Crédito", item.get().getDescription());
    }

    @Test
    void shouldLoadPisItems() {
        Optional<CSTItem> item = csvRepository.findPisByCode("01");
        assertTrue(item.isPresent());
        assertEquals("pis", item.get().getType());
    }

    @Test
    void shouldLoadCofinsItems() {
        Optional<CSTItem> item = csvRepository.findCofinsByCode("01");
        assertTrue(item.isPresent());
        assertEquals("cofins", item.get().getType());
    }

    @Test
    void shouldReturnEmptyWhenCodeNotFound() {
        Optional<CSTItem> item = csvRepository.findIpiByCode("999");
        assertFalse(item.isPresent());
    }
}
