package flab.commercemarket.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.controller.product.dto.ProductDto;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.repository.ProductRepository;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.Role;
import flab.commercemarket.domain.user.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    ProductRepository productRepository;

    @Mock
    UserService userService;

    @InjectMocks
    ProductService productService;

    User seller;

    ProductDto productDto;

    @BeforeEach
    void init() {
        seller = User.builder()
                .id(1L)
                .name("taebong")
                .email("thk98k@gmail.com")
                .role(Role.USER)
                .build();

        productDto = new ProductDto(
                "product name",
                10000,
                "product picture",
                "product description",
                seller.getId());
    }

    @Test
    @DisplayName("상품을 등록한다.")
    public void registerProduct() throws Exception {
        // given
        Product product = productFixture(1L);

        when(userService.getUserById(productDto.getSellerId())).thenReturn(seller);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // when
        Product registeredProduct = productService.registerProduct(productDto);

        // then
        assertNotNull(registeredProduct);
        assertThat(productDto.getName()).isEqualTo(registeredProduct.getName());
        assertThat(productDto.getPrice()).isEqualTo(registeredProduct.getPrice());
        assertThat(productDto.getImageUrl()).isEqualTo(registeredProduct.getImageUrl());
        assertThat(productDto.getDescription()).isEqualTo(registeredProduct.getDescription());
        assertThat(seller.getEmail()).isEqualTo(registeredProduct.getSeller().getEmail());
    }

    @Test
    @DisplayName("상품을 업데이트한다.")
    public void updateProduct_success() {
        // given
        long productId = 1L;
        Product product = productFixture(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        ProductDto changeProduct = new ProductDto(
                "change product name",
                100,
                "change product picture",
                "change product description",
                seller.getId()
        );

        // when
        Product updatedProduct = productService.updateProduct(productId, changeProduct);

        // then
        assertNotNull(updatedProduct);
        assertThat(changeProduct.getName()).isEqualTo(updatedProduct.getName());
        assertThat(changeProduct.getPrice()).isEqualTo(updatedProduct.getPrice());
        assertThat(changeProduct.getImageUrl()).isEqualTo(updatedProduct.getImageUrl());
        assertThat(changeProduct.getDescription()).isEqualTo(updatedProduct.getDescription());
        assertThat(seller.getEmail()).isEqualTo(updatedProduct.getSeller().getEmail());
    }

    @Test
    @DisplayName("업데이트할 상품이 존재하지 않을때 DataNotFoundException을 던진다.")
    public void updateProductTest_productNotFound() throws Exception {
        // given
        long productId = 1L;

        // when
        when(productRepository.findById(productId)).thenThrow(DataNotFoundException.class);

        // then
        assertThrows(DataNotFoundException.class, () -> {
            productService.updateProduct(productId, productDto);
        });
    }

    @Test
    @DisplayName("ProductId로 상품을 조회한다.")
    public void findProductTest() throws Exception {
        // given
        long productId = 1L;
        Product product = productFixture(productId);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        Product foundProduct = productService.getProductById(productId);

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
        List<Product> productList = getProductListFixture();
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
    @DisplayName("키워드로 상품 이름을 검색한다.")
    public void searchProductTest() {
        // given
        String keyword = "testKeyword";
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        List<Product> expectedProducts = new ArrayList<>();

        when(productRepository.findByKeyword(keyword, pageable))
                .thenReturn(expectedProducts);

        // when
        List<Product> actualProducts = productService.searchProduct(keyword, page, size);

        // then
        assertThat(expectedProducts).isEqualTo(actualProducts);
    }

    @Test
    public void countSearchProductByKeywordTest() {
        // given
        String keyword = "testKeyword";
        long expectedCount = 10L; // 예상되는 결과 수

        when(productRepository.countSearchProductByKeyword(keyword))
                .thenReturn(expectedCount);

        // when
        long actualCount = productService.countSearchProductByKeyword(keyword);

        // then
        assertThat(expectedCount).isEqualTo(actualCount);
    }

    @Test
    public void deleteProductTest() {
        // given
        long productId = 1L;
        Product product = new Product(); // 가상의 제품 객체

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(productId);

        // then
        verify(productRepository, times(1)).delete(eq(product));
    }

    private Product productFixture(long productId) {
        return Product.builder()
                .id(productId)
                .name(productDto.getName())
                .price(productDto.getPrice())
                .imageUrl(productDto.getImageUrl())
                .description(productDto.getDescription())
                .seller(seller)
                .build();
    }

    private List<Product> getProductListFixture() {
        Product product1 = productFixture(1);
        Product product2 = productFixture(2);
        Product product3 = productFixture(3);
        Product product4 = productFixture(4);
        Product product5 = productFixture(5);

        return Arrays.asList(product1, product2, product3, product4, product5);
    }
}
