package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HistoryServiceImplTest {

    private HistoryServiceImpl historyService;
    private BusinessException emptyException;

    @BeforeEach
    void setUp() {
        historyService = new HistoryServiceImpl();
        emptyException = new BusinessException("");
    }

    @Test
    void when_Add_WithoutHistory_NotNullResponse() {
        HistoryTest historyToTest = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .build();
        Either<BusinessException, HistoryEntity<?>> response = historyService.addHistory(historyToTest, (error, entity) -> null);
        assertNotNull(response);
        assertTrue(response.isRight());
    }

    @Test
    void when_Add_ValidFromNull_MustFail() {
        HistoryTest historyToTest = HistoryTest.builder().build();
        Either<BusinessException, HistoryEntity<?>> response = historyService.addHistory(historyToTest,
                (error, history) -> error == HistoryErrorEnum.VALID_FROM_NULL ? new BusinessException(error.name()) : null);
        assertNotNull(response);
        assertTrue(response.isLeft());
        assertEquals(response.leftOrElse(emptyException).getMessage(), HistoryErrorEnum.VALID_FROM_NULL.name());
    }

    @Test
    void when_Add_ValidUntilNotNull_MustFail() {
        HistoryTest historyToTest = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validUntil(LocalDate.of(2023, 1, 2))
                .build();
        Either<BusinessException, HistoryEntity<?>> response = historyService.addHistory(historyToTest,
                (error, history) -> error == HistoryErrorEnum.VALID_UNTIL_NOT_NULL ? new BusinessException(error.name()) : null);
        assertNotNull(response);
        assertTrue(response.isLeft());
        assertEquals(response.leftOrElse(emptyException).getMessage(), HistoryErrorEnum.VALID_UNTIL_NOT_NULL.name());
    }

    @Builder
    private static class HistoryTest implements HistoryEntity<Long> {

        private LocalDate validFrom;
        private LocalDate validUntil;
        private List<HistoryEntity<Long>> history;
        private Long id;

        @Override
        public Long id() {
            return id;
        }

        @Override
        public Optional<LocalDate> validFrom() {
            return Objects.isNull(this.validFrom) ? Optional.empty() : Optional.of(this.validFrom);
        }

        @Override
        public Optional<LocalDate> validUntil() {
            return Objects.isNull(this.validUntil) ? Optional.empty() : Optional.of(this.validUntil);
        }

        @Override
        public void validFrom(LocalDate validFrom) {
            this.validFrom = validFrom;
        }

        @Override
        public void validUntil(LocalDate validUntil) {
            this.validUntil = validUntil;
        }

        @Override
        public List<HistoryEntity<Long>> getHistory() {
            return this.history;
        }
    }

}