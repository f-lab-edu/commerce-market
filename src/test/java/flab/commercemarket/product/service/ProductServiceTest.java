package flab.commercemarket.product.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.mapper.ProductMapper;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductMapper productMapper;

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

        verify(productMapper).updateProduct(updateProduct);
        verify(productMapper).findById(productId);
    }

    @Test
    @DisplayName("업데이트할 상품이 존재하지 않을때 DataNotFoundException을 던진다.")
    public void updateProductTest_productNotFound() throws Exception {
        // given
        long productId = 1L;
        Product updatedProductData = makeProductFixture(1);

        // when
        // productMapper.findById()가 호출될 때 동작 설정
        when(productMapper.findById(productId)).thenReturn(Optional.empty());

        // then
        assertThrows(DataNotFoundException.class, () -> {
            productService.updateProduct(productId, updatedProductData);
        });
    }

    @Test
    public void searchProductTest() throws Exception {
        // given
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);
        Product product3 = makeProductFixture(3);
        Product product4 = makeProductFixture(4);

        List<Product> expectedResults = Arrays.asList(product1,product2,product3);

        // when
        when(productMapper.searchProduct("product")).thenReturn(expectedResults);

        // then
        List<Product> actualResults = productService.searchProduct("product");
        assertThat(actualResults).isEqualTo(expectedResults);
    }

    @Test
    public void deleteProductTest() throws Exception {
        // given
        long productId = 1;
        Product existProduct = makeProductFixture(1);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(existProduct));
        productService.deleteProduct(productId);

        // then
        verify(productMapper).deleteProduct(productId);
    }

    @Test
    @DisplayName("feedback이 like면 Product의 likeCount가 1 증가한다")
    public void updateLikeCountTest_Like() throws Exception {
        // given
        long productId = 123L;
        String feedback = "like";
        Product product = new Product();
        product.setLikeCount(10);


        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        productService.updateLikeCount(productId, feedback);

        assertThat(11).isEqualTo(product.getLikeCount());
    }

    @Test
    public void updateLikeCountTest_Dislike() throws Exception {
        // given
        long productId = 123L;
        String feedback = "dislike";
        Product product = new Product();
        product.setDislikeCount(1);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        productService.updateLikeCount(productId, feedback);

        // then
        assertThat(2).isEqualTo(product.getDislikeCount());
    }

    @Test
    @DisplayName("feedback이 like 또는 dislike가 아니면 예외를 발생시킨다.")
    public void update_likeCount_throw_exception() throws Exception {
        // given
        long productId = 1L;
        String feedback = "exception";
        Product product = makeProductFixture((int)productId);

        when(productMapper.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> productService.updateLikeCount(productId, feedback));
    }

    private Product makeProductFixture(int param) {
        Product product = new Product();
        product.setName("name"+param);
        product.setPrice(param*1000);
        product.setImageUrl("url" + param);
        product.setDescription("description"+param);
        product.setStockAmount(param*10);
        return product;
    }
}