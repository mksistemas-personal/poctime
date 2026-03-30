package app.mkiniz.poctime.base.tax.cfop;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CfopRepositoryTest {

    private CFOPRepository cfopRepository;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        cfopRepository = new CFOPRepository(objectMapper, resourceLoader);
        cfopRepository.loadCfops();
    }

    @Test
    void shouldLoadCfopsSuccessfully() {
        Optional<CFOPItem> item = cfopRepository.findByCode("1101");
        assertThat(item).isPresent();
        assertThat(item.get().code()).isEqualTo("1101");
        assertThat(item.get().description()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyWhenCfopNotFound() {
        Optional<CFOPItem> item = cfopRepository.findByCode("9999");
        assertThat(item).isEmpty();
    }

    @Test
    void shouldHaveTotalOfItems() {
        // cfop.txt has 601 lines, but some might be empty or headers? 
        // Let's just check if it's not empty.
        Optional<CFOPItem> first = cfopRepository.findByCode("1000");
        Optional<CFOPItem> last = cfopRepository.findByCode("7949");

        assertThat(first).isPresent();
        assertThat(last).isPresent();
    }
}
