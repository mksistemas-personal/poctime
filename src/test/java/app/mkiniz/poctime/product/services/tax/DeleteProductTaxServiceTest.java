package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.shared.business.BusinessException;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductTaxServiceTest {

    private ProductTaxDataRepository productTaxDataRepository;
    private HistoryService historyService;

    private DeleteProductTaxService deleteProductTaxService;

    @BeforeEach
    void setUp() {
        productTaxDataRepository = mock(ProductTaxDataRepository.class);
        historyService = mock(HistoryService.class);

        deleteProductTaxService = new DeleteProductTaxService(
                historyService,
                productTaxDataRepository
        );
    }

    @Test
    void shouldDeleteProductTaxSuccessfully() {
        Tsid id = Tsid.fast();
        ProductTaxData taxData = ProductTaxData.builder()
                .id(id.toLong())
                .ncm("12345678")
                .deleted(false)
                .build();

        when(productTaxDataRepository.findById(id.toLong())).thenReturn(Optional.of(taxData));

        ProductTaxResponse response = deleteProductTaxService.execute(id);

        assertNotNull(response);
        assertEquals("12345678", response.ncm());
        assertTrue(taxData.isDeleted());
        verify(productTaxDataRepository).save(taxData);
    }

    @Test
    void shouldThrowExceptionWhenProductTaxNotFound() {
        Tsid id = Tsid.fast();
        when(productTaxDataRepository.findById(id.toLong())).thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class, () -> deleteProductTaxService.execute(id));
        assertEquals(ProductConstants.PRODUCT_TAX_NOT_FOUND, exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdIsNull() {
        BusinessException exception = assertThrows(BusinessException.class, () -> deleteProductTaxService.execute(null));
        assertEquals(ProductConstants.PRODUCT_TAX_NOT_FOUND, exception.getMessage());
    }
}
