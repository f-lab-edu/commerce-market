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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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