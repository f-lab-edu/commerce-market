package flab.commercemarket.product.service;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.ForbiddenException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.mapper.ProductMapper;
import flab.commercemarket.domain.product.vo.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductMapper productMapper;

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

        verify(productMapper).insertProduct(product);
    }

    @Test
    @DisplayName("상품 수정")
    public void updateProductTest() throws Exception {
        // given
        long productId = 1L;
        Product existProduct = makeProductFixture(1);
        Product updatedProductData = makeProductFixture(2);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(existProduct));
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
        when(productMapper.findById(productId)).thenThrow(DataNotFoundException.class);


        // then
        assertThrows(DataNotFoundException.class, () -> {
            productService.updateProduct(productId, updatedProductData);
        });
    }

    @Test
    @DisplayName("ProductId로 상품을 조회한다.")
    public void findProductTest() throws Exception {
        // given
        long productId = 1L;
        Product product = makeProductFixture((int)productId);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));

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
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> expectedResults = Arrays.asList(product4);

        // when
        int offset = (page - 1) * size;
        when(productMapper.findAll(offset, size)).thenReturn(expectedResults);
        List<Product> foundProducts = productService.findProducts(page, size);

        // then
        assertThat(expectedResults).isEqualTo(foundProducts);

    }

    @Test
    @DisplayName("상품 목록의 전체 개수를 반환한다.")
    public void getProductCountTest() throws Exception {
        // given
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> expectedResults = Arrays.asList(product1, product2, product3, product4);

        // when
        when(productMapper.countProduct()).thenReturn(expectedResults.size());
        int result = productService.countProducts();

        // then
        assertThat(expectedResults.size()).isEqualTo(result);
    }

    @Test
    @DisplayName("키워드 조회 기능에 페이지네이션이 적용 되어야한다.")
    public void searchProductTest() throws Exception {
        // given
        int page = 1;
        int size = 3;

        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> expectedResults = Arrays.asList(product1, product2, product3);

        // when
        int offset = (page - 1) * size;
        when(productMapper.searchProduct("product", offset, size)).thenReturn(expectedResults);

        // then
        List<Product> actualResults = productService.searchProduct("product", page, size);
        assertThat(actualResults).isEqualTo(expectedResults);
    }

    @Test
    @DisplayName("특정 키워드로 조회한 데이터의 전체 개수를 반환한다.")
    public void searchProductCountByKeywordTest() throws Exception {
        // given
        String keyword = "computer";
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> expectedResults = Arrays.asList(product1, product2, product3, product4);

        // when
        when(productMapper.searchProductCountByKeyword(keyword)).thenReturn(expectedResults.size());
        int result = productService.countSearchProductByKeyword(keyword);

        // then
        assertThat(expectedResults.size()).isEqualTo(result);
    }

    @Test
    public void deleteProductTest() throws Exception {
        // given
        long productId = 1L;
        long loginUserId = 100L;
        Product product = makeProductFixture((int) productId);
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(authorizationHelper).checkUserAuthorization(product.getSellerId(), loginUserId);

        // when
        productService.deleteProduct(product.getId(), loginUserId);

        // then
        verify(productMapper).deleteProduct(productId);
    }

    @Test
    public void deleteProductTest_ForbiddenException() throws Exception {
        // given
        long productId = 1L;
        long loginUserId = 100L;

        Product product = makeProductFixture((int) productId);
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));

        // when
        doThrow(new ForbiddenException("유저 권한 정보가 일치하지 않음")).when(authorizationHelper).checkUserAuthorization(product.getSellerId(), loginUserId);

        // then
        assertThrows(ForbiddenException.class, () -> productService.deleteProduct(productId, loginUserId));
    }

    @Test
    @DisplayName("feedback이 like면 Product의 likeCount가 1 증가한다")
    public void updateLikeCountTest_Like() throws Exception {
        // given
        long productId = 123L;
        Product product = new Product();
        product.setLikeCount(10);


        when(productMapper.findById(productId)).thenReturn(Optional.of(product));

        productService.updateLikeCount(productId);

        assertThat(11).isEqualTo(product.getLikeCount());
    }

    private Product makeProductFixture(int param) {
        Product product = new Product();
        product.setId((long) param);
        product.setName("name"+param);
        product.setPrice(param*1000);
        product.setImageUrl("url" + param);
        product.setDescription("description"+param);
        product.setStockAmount(param*10);
        return product;
    }
}