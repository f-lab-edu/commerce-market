package flab.commercemarket.product.mapper;

import flab.commercemarket.product.domain.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(properties = "spring.config.name=application-test")
@Transactional
class ProductMapperTest {

    @Autowired
    private ProductMapper productMapper;

    @Test
    @DisplayName("데이터를 삽입한 뒤 조회하여 일치여부를 판단합니다")
    void insertProductTest() {

        // given
        Product product = new Product();
        product.setProductName("product1");
        product.setPrice(1000);
        product.setImageUrl("url1");
        product.setDescription("description1");
        product.setStockAmount(1);
        product.setSalesAmount(1);
        product.setLikeCount(1);
        product.setDislikeCount(1);
        product.setSellerId(2);

        // when
        productMapper.insertProduct(product);
        Long getId = product.getId();
        Optional<Product> optionalProduct = productMapper.findById(getId);

        // then
        assertThat(optionalProduct).isPresent();
        Product findProduct = optionalProduct.get();
        assertThat(findProduct.getProductName()).isEqualTo(product.getProductName());
    }

    @Test
    @DisplayName("상품을 업데이트한 뒤 변경된 정보가 정확히 반영되었는지 확인합니다")
    void updateProductTest() {
        // 새로운 상품 객체 생성
        Product product = new Product();
        product.setProductName("product1");
        product.setPrice(1000);
        product.setImageUrl("url1");
        product.setDescription("description1");
        product.setStockAmount(1);

        // 상품을 데이터베이스에 삽입
        productMapper.insertProduct(product);
        Long id = product.getId();

        // 업데이트할 상품 정보 설정
        Product updatedProduct = new Product();
        updatedProduct.setId(id);
        updatedProduct.setProductName("updatedProduct");
        updatedProduct.setPrice(2000);
        updatedProduct.setImageUrl("updatedUrl");
        updatedProduct.setDescription("updatedDescription");
        updatedProduct.setStockAmount(2);

        // 상품 정보 업데이트
        productMapper.updateProduct(updatedProduct);

        // 업데이트된 상품을 조회하여 변경된 정보 확인
        Optional<Product> optionalProduct = productMapper.findById(id);
        assertThat(optionalProduct).isPresent();
        Product retrievedProduct = optionalProduct.get();

        assertNotNull(retrievedProduct);
        assertEquals(updatedProduct.getProductName(), retrievedProduct.getProductName());
        assertEquals(updatedProduct.getPrice(), retrievedProduct.getPrice());
        assertEquals(updatedProduct.getImageUrl(), retrievedProduct.getImageUrl());
        assertEquals(updatedProduct.getDescription(), retrievedProduct.getDescription());
        assertEquals(updatedProduct.getStockAmount(), retrievedProduct.getStockAmount());
    }

    @Test
    @DisplayName("모든 상품을 오프셋과 사이즈에 맞게 조회하여 반환합니다")
    void findAll() {
        // given
        List<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Product product = new Product();
            product.setProductName("Product " + i);
            product.setPrice(1000 * i);
            product.setImageUrl("url " + i);
            product.setDescription("Description " + i);
            product.setStockAmount(i);
            products.add(product);
            productMapper.insertProduct(product);
        }

        int offset = 2;
        int size = 5;

        List<Product> retrievedProducts = productMapper.findAll(offset, size);

        for (int i = 0; i < size; i++) {
            Product expectedProduct = products.get(offset + i);
            Product retrievedProduct = retrievedProducts.get(i);
            assertEquals(expectedProduct.getProductName(), retrievedProduct.getProductName());
            assertEquals(expectedProduct.getPrice(), retrievedProduct.getPrice());
            assertEquals(expectedProduct.getImageUrl(), retrievedProduct.getImageUrl());
            assertEquals(expectedProduct.getDescription(), retrievedProduct.getDescription());
            assertEquals(expectedProduct.getStockAmount(), retrievedProduct.getStockAmount());
        }
    }

    @Test
    @DisplayName("키워드에 해당하는 상품을 검색하여 반환합니다")
    void searchProduct() {
        Product product1 = new Product();
        product1.setProductName("Apple");
        product1.setPrice(1000);
        product1.setImageUrl("url1");
        product1.setDescription("Description 1");
        product1.setStockAmount(1);
        productMapper.insertProduct(product1);

        Product product2 = new Product();
        product2.setProductName("Banana");
        product2.setPrice(2000);
        product2.setImageUrl("url2");
        product2.setDescription("Description 2");
        product2.setStockAmount(2);
        productMapper.insertProduct(product2);

        String keyword = "Apple";

        List<Product> searchResults = productMapper.searchProduct(keyword);

        assertNotNull(searchResults);

        assertEquals(1, searchResults.size());
        Product retrievedProduct = searchResults.get(0);
        assertEquals(product1.getProductName(), retrievedProduct.getProductName());
        assertEquals(product1.getPrice(), retrievedProduct.getPrice());
        assertEquals(product1.getImageUrl(), retrievedProduct.getImageUrl());
        assertEquals(product1.getDescription(), retrievedProduct.getDescription());
        assertEquals(product1.getStockAmount(), retrievedProduct.getStockAmount());
    }

    @Test
    @DisplayName("상품을 삭제한 뒤 해당 상품이 더 이상 존재하지 않는지 확인합니다")
    void deleteProduct() {
        // 새로운 상품 객체 생성
        Product product = new Product();
        product.setProductName("product1");
        product.setPrice(1000);
        product.setImageUrl("url1");
        product.setDescription("description1");
        product.setStockAmount(1);
        product.setSalesAmount(1);
        product.setLikeCount(1);
        product.setDislikeCount(1);
        product.setSellerId(2);

        // 상품을 데이터베이스에 삽입
        productMapper.insertProduct(product);
        Long id = product.getId();

        productMapper.deleteProduct(id);

        Optional<Product> optionalProduct = productMapper.findById(id);
        assertThat(optionalProduct).isEmpty();
    }
}