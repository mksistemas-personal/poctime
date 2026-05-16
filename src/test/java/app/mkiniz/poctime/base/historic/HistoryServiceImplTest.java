package app.mkiniz.poctime.base.historic;

import app.mkiniz.poctime.shared.business.BusinessException;
import cyclops.control.Either;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

class HistoryServiceImplTest {

    private static final BiFunction<HistoryErrorEnum, HistoryEntity, BusinessException> GENERATE_EXCEPTION =
            (error, entity) -> Objects.nonNull(error) ? new BusinessException(error.name()) : null;

    private HistoryServiceImpl historyService;
    private BusinessException emptyException;

    @BeforeEach
    void setUp() {
        historyService = new HistoryServiceImpl();
        emptyException = new BusinessException("");
    }


    @Test
    void when_Add_Entity_NullException() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> historyService.addHistory(null, GENERATE_EXCEPTION));
        assertEquals("history.entity.is.null", exception.getMessage());
    }

    @Test
    void when_Add_GenerateException_NullException() {
        HistoryTest historyToTest = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .build();
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> historyService.addHistory(historyToTest, null));
        assertEquals("history.generate.business.exception.is.null", exception.getMessage());
    }


    @Test
    void when_Add_WithoutHistory_NotNullResponse() {
        HistoryTest historyToTest = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .build();
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTest, GENERATE_EXCEPTION);
        assertNotNull(response);
        assertTrue(response.isRight());
    }

    @Test
    void when_Add_ValidFromNull_MustFail() {
        HistoryTest historyToTest = HistoryTest.builder().build();
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTest, GENERATE_EXCEPTION);
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
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTest, GENERATE_EXCEPTION);
        assertNotNull(response);
        assertTrue(response.isLeft());
        assertEquals(response.leftOrElse(emptyException).getMessage(), HistoryErrorEnum.VALID_UNTIL_NOT_NULL.name());
    }

    @Test
    void when_Add_ValidFrom_SmallerThanLastValidFrom() {
        HistoryTest historyToTestList = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 2))
                .build();
        HistoryTest historyToTest = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .history(List.of(historyToTestList))
                .build();
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTest, GENERATE_EXCEPTION);
        assertNotNull(response);
        assertTrue(response.isLeft());
        assertEquals(response.leftOrElse(emptyException).getMessage(),
                HistoryErrorEnum.VALID_FROM_MUST_BE_GREATER_THAN_LAST_ENTRY.name());
    }

    @Test
    void when_Add_MustReturnHistoryAdded() {
        HistoryTest historyToTestListFirst = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validUntil(LocalDate.of(2023, 1, 31))
                .build();
        HistoryTest historyToTestListLast = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 2, 1))
                .build();
        HistoryTest historyToTestNew = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 3, 1))
                .history(List.of(historyToTestListFirst, historyToTestListLast))
                .build();
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTestNew, GENERATE_EXCEPTION);
        assertNotNull(response);
        assertTrue(response.isRight());
        HistoryService.HistoryAdded added = response.orElseGet(null);
        assertEquals(added.adjustedEntity().validUntil(), historyToTestNew.validFrom().minusDays(1));
        assertEquals(added.newEntity().validFrom(), historyToTestNew.validFrom());
        assertNull(added.newEntity().validUntil());
    }

    @Test
    void when_Add_MustReturnError() {
        HistoryTest historyToTestListFirst = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validUntil(LocalDate.of(2023, 1, 31))
                .build();
        HistoryTest historyToTestListLast = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 2, 1))
                .build();
        HistoryTest historyToTestNew = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 2, 1))
                .history(List.of(historyToTestListFirst, historyToTestListLast))
                .build();
        Either<BusinessException, HistoryService.HistoryAdded> response = historyService.addHistory(historyToTestNew, GENERATE_EXCEPTION);
        assertNotNull(response);
        assertTrue(response.isLeft());
        assertEquals(response.leftOrElse(emptyException).getMessage(),
                HistoryErrorEnum.VALID_FROM_MUST_BE_GREATER_THAN_LAST_ENTRY.name());

    }

    @Test
    void when_Update_ValidFromDiffers_MustFail() {
        HistoryTest entity = HistoryTest.builder().validFrom(LocalDate.of(2023, 1, 1)).build();
        Either<BusinessException, HistoryEntity> response = historyService.updateHistory(entity, LocalDate.of(2023, 1, 2), null, GENERATE_EXCEPTION);
        assertTrue(response.isLeft());
        assertEquals(HistoryErrorEnum.VALID_FROM_MUST_BE_EQUAL_TO_VALID_FROM.name(), response.leftOrElse(emptyException).getMessage());
    }

    @Test
    void when_Update_ValidUntilDiffers_MustFail() {
        HistoryTest entity = HistoryTest.builder()
                .validFrom(LocalDate.of(2023, 1, 1))
                .validUntil(LocalDate.of(2023, 1, 31))
                .build();
        Either<BusinessException, HistoryEntity> response = historyService.updateHistory(entity, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 2, 1), GENERATE_EXCEPTION);
        assertTrue(response.isLeft());
        assertEquals(HistoryErrorEnum.VALID_UNTIL_MUST_BE_EQUAL_TO_VALID_UNTIL.name(), response.leftOrElse(emptyException).getMessage());
    }

    @Test
    void when_Update_Valid_MustReturnEntity() {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate until = LocalDate.of(2023, 1, 31);
        HistoryTest entity = HistoryTest.builder().validFrom(from).validUntil(until).build();
        Either<BusinessException, HistoryEntity> response = historyService.updateHistory(entity, from, until, GENERATE_EXCEPTION);
        assertTrue(response.isRight());
        assertEquals(entity, response.orElse(null));
    }

    @Test
    void when_Adjust_FirstDeleted_MustAdjustNext() {
        LocalDate d1 = LocalDate.of(2023, 1, 1);
        LocalDate d2 = LocalDate.of(2023, 2, 1);
        LocalDate d3 = LocalDate.of(2023, 3, 1);

        HistoryTest e1 = HistoryTest.builder().validFrom(d1).validUntil(d2.minusDays(1)).build();
        HistoryTest e2 = HistoryTest.builder().validFrom(d2).validUntil(d3.minusDays(1)).build();
        HistoryTest e3 = HistoryTest.builder().validFrom(d3).validUntil(null).build();

        List<HistoryEntity> history = List.of(e1, e2, e3);
        e1.history = history;
        e2.history = history;
        e3.history = history;

        HistoryEntity result = historyService.adjustFromDeletedHistory(e1);
        assertEquals(e2, result);
        assertEquals(d1, e2.validFrom());
    }

    @Test
    void when_Adjust_LastDeleted_MustAdjustPrevious() {
        LocalDate d1 = LocalDate.of(2023, 1, 1);
        LocalDate d2 = LocalDate.of(2023, 2, 1);

        HistoryTest e1 = HistoryTest.builder().validFrom(d1).validUntil(d2.minusDays(1)).build();
        HistoryTest e2 = HistoryTest.builder().validFrom(d2).validUntil(null).build();

        List<HistoryEntity> history = List.of(e1, e2);
        e1.history = history;
        e2.history = history;

        HistoryEntity result = historyService.adjustFromDeletedHistory(e2);
        assertEquals(e1, result);
        assertNull(e1.validUntil());
    }

    @Test
    void when_Adjust_MiddleDeleted_MustAdjustPrevious() {
        LocalDate d1 = LocalDate.of(2023, 1, 1);
        LocalDate d2 = LocalDate.of(2023, 2, 1);
        LocalDate d3 = LocalDate.of(2023, 3, 1);

        HistoryTest e1 = HistoryTest.builder().validFrom(d1).validUntil(d2.minusDays(1)).build();
        HistoryTest e2 = HistoryTest.builder().validFrom(d2).validUntil(d3.minusDays(1)).build();
        HistoryTest e3 = HistoryTest.builder().validFrom(d3).validUntil(null).build();

        List<HistoryEntity> history = List.of(e1, e2, e3);
        e1.history = history;
        e2.history = history;
        e3.history = history;

        HistoryEntity result = historyService.adjustFromDeletedHistory(e2);
        assertEquals(e1, result);
        assertEquals(d2, e1.validUntil());
    }

    @Builder
    private static class HistoryTest implements HistoryEntity {

        private LocalDate validFrom;
        private LocalDate validUntil;
        private List<HistoryEntity> history;

        @Override
        public LocalDate validFrom() {
            return this.validFrom;
        }

        @Override
        public LocalDate validUntil() {
            return this.validUntil;
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
        public List<HistoryEntity> getHistory() {
            return Objects.isNull(this.history) ? List.of() : this.history;
        }
    }

}