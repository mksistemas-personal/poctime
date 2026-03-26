package app.mkiniz.poctime.product.services;

import app.mkiniz.poctime.product.domain.*;
import app.mkiniz.poctime.product.domain.category.Category;
import app.mkiniz.poctime.product.domain.category.CategoryRepository;
import app.mkiniz.poctime.shared.UpdateBaseBusinessTest;
import app.mkiniz.poctime.shared.adapter.TsidGenerator;
import com.github.f4b6a3.tsid.Tsid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TsidGenerator tsidGenerator;

    @InjectMocks
    private UpdateProductService updateProductService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    private UpdateBaseBusinessTest<Tsid, UpdateProductRequest, ProductResponse> baseTest;
    private Tsid productId;
    private Tsid categoryId;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        this.baseTest = UpdateBaseBusinessTest.of();
        TsidGenerator generator = new TsidGenerator();
        this.productId = generator.newTsid();
        this.categoryId = generator.newTsid();

        this.category = Category.builder()
                .id(categoryId.toLong())
                .name("Existing Category")
                .build();

        this.product = Product.builder()
                .id(productId.toLong())
                .name("Old Name")
                .sku("OLD-SKU")
                .description("Old Description")
                .category(category)
                .deleted(false)
                .build();
    }

    @Test
    void updateProductWithExistingCategory() {
        this.baseTest
                .given(() -> {
                    when(productRepository.findById(productId.toLong())).thenReturn(Optional.of(product));
                    when(categoryRepository.findById(categoryId.toLong())).thenReturn(Optional.of(category));
                    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
                    return new UpdateProductRequest("New Name", new CategoryRequest(categoryId, "Existing Category"), "NEW-SKU", "New Description");
                })
                .when((id, request) -> updateProductService.execute(id, request))
                .then((id, request, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response.name()).isEqualTo("New Name");
                    assertThat(response.sku()).isEqualTo("NEW-SKU");
                    assertThat(response.description()).isEqualTo("New Description");
                    assertThat(response.category().id()).isEqualTo(categoryId.toLowerCase());

                    verify(productRepository, times(1)).findById(id.toLong());
                    verify(categoryRepository, times(1)).findById(categoryId.toLong());
                    verify(productRepository, times(1)).save(productCaptor.capture());

                    Product savedProduct = productCaptor.getValue();
                    assertThat(savedProduct.getName()).isEqualTo("New Name");
                    assertThat(savedProduct.getSku()).isEqualTo("NEW-SKU");
                })
                .execute(productId);
    }

    @Test
    void updateProductWithNewCategory() {
        Tsid newCategoryId = new TsidGenerator().newTsid();
        Category newCategory = Category.builder()
                .id(newCategoryId.toLong())
                .name("New Category")
                .build();

        this.baseTest
                .given(() -> {
                    when(productRepository.findById(productId.toLong())).thenReturn(Optional.of(product));
                    when(tsidGenerator.newIdAsLong()).thenReturn(newCategoryId.toLong());
                    when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
                    when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));
                    return new UpdateProductRequest("New Name", new CategoryRequest(null, "New Category"), "NEW-SKU", "New Description");
                })
                .when((id, request) -> updateProductService.execute(id, request))
                .then((id, request, response) -> {
                    assertThat(response).isNotNull();
                    assertThat(response.category().name()).isEqualTo("New Category");

                    verify(categoryRepository, times(1)).save(any(Category.class));
                    verify(productRepository, times(1)).save(any(Product.class));
                })
                .execute(productId);
    }
}
