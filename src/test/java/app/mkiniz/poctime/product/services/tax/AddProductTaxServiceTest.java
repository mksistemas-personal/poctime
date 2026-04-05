package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.historic.HistoryService;
import app.mkiniz.poctime.base.historic.HistoryServiceImpl;
import app.mkiniz.poctime.base.tax.cfop.CFOPRepository;
import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMItem;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.tax.CreateProductTaxRequest;
import app.mkiniz.poctime.product.domain.tax.ProductTaxData;
import app.mkiniz.poctime.product.domain.tax.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.tax.ProductTaxResponse;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import app.mkiniz.poctime.shared.business.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductTaxServiceTest {

    private ProductTaxDataRepository productTaxDataRepository;
    private ProductRepository productRepository;
    private TsidGenerator tsidGenerator;
    private NCMService ncmService;
    private CSTRepository csvRepository;
    private CFOPRepository cfopRepository;
    private HistoryService historyService;

    private AddProductTaxService addProductTaxService;

    private Product product;
    private CreateProductTaxRequest request;

    @BeforeEach
    void setUp() {
        productTaxDataRepository = mock(ProductTaxDataRepository.class);
        productRepository = mock(ProductRepository.class);
        ncmService = mock(NCMService.class);
        tsidGenerator = new TsidGenerator();
        csvRepository = new CSTRepository(new ObjectMapper(), new DefaultResourceLoader());
        csvRepository.loadCsv();
        cfopRepository = new CFOPRepository(new ObjectMapper(), new DefaultResourceLoader());
        cfopRepository.loadCfops();
        historyService = new HistoryServiceImpl();

        addProductTaxService = new AddProductTaxService(
                ncmService,
                csvRepository,
                cfopRepository,
                historyService,
                productTaxDataRepository,
                productRepository,
                tsidGenerator
        );

        Tsid productId = Tsid.fast();
        product = new Product();
        product.setId(productId.toLong());

        request = CreateProductTaxRequest.builder()
                .id(productId)
                .productId(productId)
                .ncm("12345678")
                .cstIpi("00")
                .cstPis("01")
                .cstCofins("01")
                .cfop("5102")
                .validFrom(java.time.LocalDate.now())
                .build();
    }

    @Test
    void shouldAddProductTaxSuccessfully() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(ncmService.findByCode("12345678")).thenReturn(Optional.of(new NCMItem("12345678", "Desc", "2024-01-01", "", "Ato", "1", "2024")));
        lenient().when(productTaxDataRepository.save(any(ProductTaxData.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(productTaxDataRepository.save(null)).thenAnswer(invocation -> null);

        ProductTaxResponse response = addProductTaxService.execute(request);

        assertNotNull(response);
        assertEquals("12345678", response.ncm());
        assertEquals("00", response.cstIpi());
        assertEquals("01", response.cstPis());
        assertEquals("01", response.cstCofins());
        verify(productTaxDataRepository, atLeastOnce()).save(any(ProductTaxData.class));
    }

    @Test
    void shouldCloseOldTaxDataWhenAddingNewOne() {
        ProductTaxData oldTaxData = ProductTaxData.builder()
                .id(1L)
                .product(product)
                .ncm("00000000")
                .validFrom(java.time.LocalDate.now().minusDays(10))
                .validUntil(null)
                .build();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        product.setTaxDataHistory(new java.util.ArrayList<>(java.util.List.of(oldTaxData)));
        when(ncmService.findByCode("12345678")).thenReturn(Optional.of(new NCMItem("12345678", "Desc", "2024-01-01", "", "Ato", "1", "2024")));
        lenient().when(productTaxDataRepository.save(any(ProductTaxData.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(productTaxDataRepository.save(null)).thenAnswer(invocation -> null);

        java.time.LocalDate newValidFrom = java.time.LocalDate.now();
        request = CreateProductTaxRequest.builder()
                .id(Tsid.from(product.getId()))
                .productId(Tsid.from(product.getId()))
                .ncm("12345678")
                .cstIpi("00")
                .cstPis("01")
                .cstCofins("01")
                .cfop("5102")
                .validFrom(newValidFrom)
                .build();

        addProductTaxService.execute(request);

        assertNotNull(oldTaxData.getValidUntil());
        assertEquals(newValidFrom.minusDays(1), oldTaxData.getValidUntil());
        verify(productTaxDataRepository).save(oldTaxData);
        verify(productTaxDataRepository).save(argThat(tax -> tax != null && tax.getValidFrom().equals(newValidFrom)));
    }

    @Test
    void shouldThrowExceptionWhenCstIpiNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(ncmService.findByCode("12345678")).thenReturn(Optional.of(new NCMItem("12345678", "Desc", "2024-01-01", "", "Ato", "1", "2024")));
        request = CreateProductTaxRequest.builder()
                .id(Tsid.fast())
                .productId(Tsid.fast())
                .ncm("12345678")
                .cstIpi("INVALID")
                .cstPis("01")
                .cstCofins("01")
                .cfop("5102")
                .validFrom(java.time.LocalDate.now())
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> addProductTaxService.execute(request));
        assertEquals(ProductConstants.PRODUCT_TAX_CST_IPI_NOT_FOUND, exception.getMessage());
    }
}
