package app.mkiniz.poctime.product.services.tax;

import app.mkiniz.poctime.base.tax.cst.CSTRepository;
import app.mkiniz.poctime.base.tax.ncm.NCMItem;
import app.mkiniz.poctime.base.tax.ncm.NCMService;
import app.mkiniz.poctime.product.ProductConstants;
import app.mkiniz.poctime.product.domain.Product;
import app.mkiniz.poctime.product.domain.ProductRepository;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxData;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxDataRepository;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxRequest;
import app.mkiniz.poctime.product.domain.taxdata.ProductTaxResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddProductTaxServiceTest {

    private ProductTaxDataRepository productTaxDataRepository;
    private ProductRepository productRepository;
    private TsidGenerator tsidGenerator;
    private NCMService ncmService;
    private CSTRepository csvRepository;

    private AddProductTaxService addProductTaxService;

    private Product product;
    private ProductTaxRequest request;

    @BeforeEach
    void setUp() {
        productTaxDataRepository = mock(ProductTaxDataRepository.class);
        productRepository = mock(ProductRepository.class);
        ncmService = mock(NCMService.class);
        tsidGenerator = new TsidGenerator();
        csvRepository = new CSTRepository(new ObjectMapper(), new DefaultResourceLoader());
        csvRepository.loadCsv();

        addProductTaxService = new AddProductTaxService(
                productTaxDataRepository,
                productRepository,
                tsidGenerator,
                ncmService,
                csvRepository
        );

        Tsid productId = Tsid.fast();
        product = new Product();
        product.setId(productId.toLong());

        request = ProductTaxRequest.builder()
                .id(productId)
                .productId(productId)
                .ncm("12345678")
                .cstIpi("00")
                .cstPis("01")
                .cstCofins("01")
                .cfop("5102")
                .build();
    }

    @Test
    void shouldAddProductTaxSuccessfully() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(ncmService.findByCode("12345678")).thenReturn(Optional.of(new NCMItem("12345678", "Desc", "2024-01-01", "", "Ato", "1", "2024")));
        when(productTaxDataRepository.save(any(ProductTaxData.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductTaxResponse response = addProductTaxService.execute(request);

        assertNotNull(response);
        assertEquals("12345678", response.ncm());
        assertEquals("00", response.cstIpi());
        assertEquals("01", response.cstPis());
        assertEquals("01", response.cstCofins());
        verify(productTaxDataRepository).save(any(ProductTaxData.class));
    }

    @Test
    void shouldThrowExceptionWhenCstIpiNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(ncmService.findByCode("12345678")).thenReturn(Optional.of(new NCMItem("12345678", "Desc", "2024-01-01", "", "Ato", "1", "2024")));
        request = ProductTaxRequest.builder()
                .id(Tsid.fast())
                .productId(Tsid.fast())
                .ncm("12345678")
                .cstIpi("INVALID")
                .cstPis("01")
                .cstCofins("01")
                .cfop("5102")
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> addProductTaxService.execute(request));
        assertEquals(ProductConstants.CST_NOT_FOUND, exception.getMessage());
    }
}
