package flab.commercemarket.product.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        Product product = new Product(1L, "product1", 1000, "url1", "description1", 1, 1, 1,1,2);

        // when
        Product registerProduct = productService.registerProduct(product);

        // then
        assertThat(registerProduct).isNotNull();
        assertThat(product.getProductName()).isEqualTo(registerProduct.getProductName());
        assertThat(product.getPrice()).isEqualTo(registerProduct.getPrice());

        Mockito.verify(productMapper).insertProduct(product);
    }

    @Test
    @DisplayName("상품 수정")
    public void updateProductTest() throws Exception {
        // given
        long productId = 1L;
        Product existProduct = new Product(productId, "ExistingProduct", 1000, "Existing url", "Existing description", 1, 1, 1,1,2);
        Product updatedProductData = new Product(productId, "UpdatedProduct", 2000, "Updated url", "Updated description", 2, 1, 1,1,2);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(existProduct));
        Product updateProduct = productService.updateProduct(productId, updatedProductData);

        // then
        assertThat(updateProduct).isNotNull();
        assertThat(updatedProductData.getProductName()).isEqualTo(updateProduct.getProductName());
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
        Product updatedProductData = new Product(productId, "UpdatedProduct", 2000, "Updated url", "Updated description", 2, 1, 1,1,2);

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
        Product product1 = new Product(1L, "product1", 1000, "url1", "description1", 1,1,1,1,1);
        Product product2 = new Product(2L, "product2", 2000, "url2", "description2", 1,1,1,1,1);
        Product product3 = new Product(3L, "product3", 3000, "url3", "description3", 1,1,1,1,1);
        Product product4 = new Product(4L, "TEST", 4000, "url3", "description3", 1,1,1,1,1);
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
        Product existProduct = new Product(productId, "ExistingProduct", 1000, "Existing url", "Existing description", 1, 1, 1,1,2);

        // when
        when(productMapper.findById(productId)).thenReturn(Optional.of(existProduct));
        productService.deleteProduct(productId);

        // then
        verify(productMapper).deleteProduct(productId);
    }
}