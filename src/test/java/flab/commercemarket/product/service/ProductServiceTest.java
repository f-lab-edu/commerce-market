package flab.commercemarket.product.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.repository.ProductRepository;
import flab.commercemarket.domain.product.vo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthorizationHelper authorizationHelper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("상품 등록")
    public void registerProductTest() throws Exception {
        // given
        Product product = makeProductFixture(1);

        // when
        Product registerProduct = productService.registerProduct(product);

        // then
        assertThat(registerProduct).isNotNull();
        assertThat(product.getName()).isEqualTo(registerProduct.getName());
        assertThat(product.getPrice()).isEqualTo(registerProduct.getPrice());
        assertThat(product.getImageUrl()).isEqualTo(registerProduct.getImageUrl());
        assertThat(product.getDescription()).isEqualTo(registerProduct.getDescription());
        assertThat(product.getStockAmount()).isEqualTo(registerProduct.getStockAmount());

        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("상품 수정")
    public void updateProductTest() throws Exception {
        // given
        long productId = 1L;
        Product existProduct = makeProductFixture(1);
        Product updatedProductData = makeProductFixture(2);

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(existProduct));
        Product updateProduct = productService.updateProduct(productId, updatedProductData);

        // then
        assertThat(updateProduct).isNotNull();
        assertThat(updatedProductData.getName()).isEqualTo(updateProduct.getName());
        assertThat(updatedProductData.getPrice()).isEqualTo(updateProduct.getPrice());
        assertThat(updatedProductData.getImageUrl()).isEqualTo(updateProduct.getImageUrl());
        assertThat(updatedProductData.getDescription()).isEqualTo(updateProduct.getDescription());
        assertThat(updatedProductData.getStockAmount()).isEqualTo(updateProduct.getStockAmount());

    }

    @Test
    @DisplayName("업데이트할 상품이 존재하지 않을때 DataNotFoundException을 던진다.")
    public void updateProductTest_productNotFound() throws Exception {
        // given
        long productId = 1L;
        Product updatedProductData = makeProductFixture(1);

        // when
        when(productRepository.findById(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            productService.updateProduct(productId, updatedProductData);
        });
    }

    @Test
    @DisplayName("상품을 등록한 사용자와 update를 요청하는 사용자가 다르면 예외를 발생시켜야한다.")
    public void updateProductTest_Forbidden_exception() throws Exception {
        // given
        long productId = 1L;
        Product foundProduct = Product.builder().id(productId).sellerId(1L).build();
        Product data = Product.builder().id(productId).sellerId(100L).build();

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(foundProduct));
        doThrow(ForbiddenException.class)
                .when(authorizationHelper)
                .checkUserAuthorization(foundProduct.getSellerId(), data.getSellerId());

        // then
        assertThrows(ForbiddenException.class, () -> productService.updateProduct(productId, data));
    }

    @Test
    @DisplayName("ProductId로 상품을 조회한다.")
    public void findProductTest() throws Exception {
        // given
        long productId = 1L;
        Product product = makeProductFixture((int)productId);

        // when
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Product foundProduct = productService.getProduct(productId);

        // then
        assertThat(product).isEqualTo(foundProduct);
    }

    @Test
    @DisplayName("상품 목록을 전체 조회하면 페이지네이션이 적용되어야 한다.")
    public void findProductsTest() throws Exception {
        // given
        int page = 2;
        int size = 3;
        Pageable pageable = PageRequest.of(page - 1, size);

        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> productList = Arrays.asList(product4);

        Page<Product> expectedPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<Product> resultPage = productService.findProducts(page, size);

        // Then
        assertThat(resultPage.getContent()).isEqualTo(productList);
        assertThat(resultPage.getPageable().getPageNumber()).isEqualTo(page - 1);
        assertThat(resultPage.getPageable().getPageSize()).isEqualTo(size);
    }

    @Test
    void searchProductTest() {
        // Given
        int page = 2;
        int size = 3;
        String keyword = "example";
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Product> productList = productListFixture();

        Page<Product> expectedPage = new PageImpl<>(productList, pageable, productList.size());

        when(productRepository.findByKeyword(pageable, keyword)).thenReturn(expectedPage);

        // When
        Page<Product> resultPage = productService.searchProduct(keyword, page, size);

        // Then
        assertThat(resultPage.getContent()).isEqualTo(productList);
        assertThat(resultPage.getPageable().getPageNumber()).isEqualTo(page - 1);
        assertThat(resultPage.getPageable().getPageSize()).isEqualTo(size);
    }

    @Test
    public void deleteProductTest() throws Exception {
        // given
        long productId = 1L;
        long loginUserId = 100L;
        Product product = makeProductFixture((int) productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(authorizationHelper).checkUserAuthorization(product.getSellerId(), loginUserId);

        // when
        productService.deleteProduct(product.getId(), loginUserId);

        // then
        verify(productRepository).delete(product);
    }

    @Test
    public void deleteProductTest_ForbiddenException() throws Exception {
        // given
        long productId = 1L;
        long loginUserId = 100L;

        Product product = makeProductFixture((int) productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        doThrow(new ForbiddenException("유저 권한 정보가 일치하지 않음")).when(authorizationHelper).checkUserAuthorization(product.getSellerId(), loginUserId);

        // then
        assertThrows(ForbiddenException.class, () -> productService.deleteProduct(productId, loginUserId));
    }

    @Test
    public void updateLikeCountTest_Like() throws Exception {
        // given
        long productId = 123L;
        Product product = Product.builder().id(productId).likeCount(10).build();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        productService.updateLikeCount(productId);

        verify(productRepository, times(1)).updateLikeCount(productId, product.getLikeCount() + 1);
    }

    private Product makeProductFixture(int param) {
        Product product = new Product();
        product.setId((long) param);
        product.setName("name"+param);
        product.setPrice(param*1000);
        product.setImageUrl("url" + param);
        product.setDescription("description"+param);
        product.setStockAmount(param*10);
        product.setSellerId(param);
        return product;
    }

    private List<Product> productListFixture() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Example Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        Product product3 = new Product();
        product3.setId(3L);
        product3.setName("Example Product 3");

        return Arrays.asList(product1, product2, product3);
    }
}