package flab.commercemarket.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;
import flab.commercemarket.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    void postProductTest() throws Exception {
        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.registerProduct(any(Product.class))).thenReturn(product);


        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);
        ResultActions perform = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void patchProductTest() throws Exception {
        long id = 1L;

        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        product.setId(id);
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.updateProduct(eq(id), any(Product.class))).thenReturn(product);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);

        ResultActions perform = mockMvc.perform(patch("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProductTest() throws Exception {
        long id = 1L;

        ProductDto productDto = makeProductDtoFixture(1);
        Product product = productDto.toProduct();
        product.setId(id);
        ProductResponseDto productResponseDto = product.toProductResponseDto();

        when(productService.findProduct(eq(id))).thenReturn(product);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);

        ResultActions perform = mockMvc.perform(get("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProductsTest() throws Exception {
        int page = 1;
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);

        List<Product> expectedProducts = Arrays.asList(
                product1, product2
        );

        when(productService.findProducts(page)).thenReturn(expectedProducts);

        mockMvc.perform(get("/products")
                        .param("page", String.valueOf(page))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedProducts.size()))
                .andExpect(jsonPath("$[0].id").value(product1.getId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[0].imageUrl").value(product1.getImageUrl()))
                .andExpect(jsonPath("$[0].description").value(product1.getDescription()))
                .andExpect(jsonPath("$[0].stockAmount").value(product1.getStockAmount()))
                .andExpect(jsonPath("$[1].id").value(product2.getId()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()))
                .andExpect(jsonPath("$[1].price").value(product2.getPrice()))
                .andExpect(jsonPath("$[1].imageUrl").value(product2.getImageUrl()))
                .andExpect(jsonPath("$[1].description").value(product2.getDescription()))
                .andExpect(jsonPath("$[1].stockAmount").value(product2.getStockAmount()));
    }

    @Test
    void searchProductTest() throws Exception {
        String keyword = "product";
        Product product1 = makeProductFixture(1);
        Product product2 = makeProductFixture(2);

        List<Product> expectedProducts = Arrays.asList(product1, product2);

        when(productService.searchProduct(keyword)).thenReturn(expectedProducts);

        mockMvc.perform(get("/products/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(expectedProducts.size()))
                .andExpect(jsonPath("$[0].id").value(product1.getId()))
                .andExpect(jsonPath("$[0].name").value(product1.getName()))
                .andExpect(jsonPath("$[0].price").value(product1.getPrice()))
                .andExpect(jsonPath("$[0].imageUrl").value(product1.getImageUrl()))
                .andExpect(jsonPath("$[0].description").value(product1.getDescription()))
                .andExpect(jsonPath("$[0].stockAmount").value(product1.getStockAmount()))
                .andExpect(jsonPath("$[1].id").value(product2.getId()))
                .andExpect(jsonPath("$[1].name").value(product2.getName()))
                .andExpect(jsonPath("$[1].price").value(product2.getPrice()))
                .andExpect(jsonPath("$[1].imageUrl").value(product2.getImageUrl()))
                .andExpect(jsonPath("$[1].description").value(product2.getDescription()))
                .andExpect(jsonPath("$[1].stockAmount").value(product2.getStockAmount()));
    }

    @Test
    void deleteProduct() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isOk());

        verify(productService).deleteProduct(id);
    }

    private ProductDto makeProductDtoFixture(int param) {
        ProductDto productDto = new ProductDto();
        productDto.setName("name " + param);
        productDto.setPrice(param * 1000);
        productDto.setImageUrl("url " + param);
        productDto.setDescription("description " + param);
        productDto.setStockAmount(param * 10);
        return productDto;
    }

    private Product makeProductFixture(int param) {
        Product product = new Product();
        product.setId((long) param);
        product.setName("name " + param);
        product.setPrice(param * 1000);
        product.setImageUrl("url " + param);
        product.setDescription("description " + param);
        product.setStockAmount(param * 10);
        return product;
    }
}