package flab.commercemarket.product.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import flab.commercemarket.product.converter.ProductConverter;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;
import flab.commercemarket.product.service.ProductService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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

    @Mock
    private ProductConverter productConverter;

    @BeforeEach
    public void init() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService, productConverter)).build();
    }

    @Test
    void postProduct() throws Exception {

        // Mock converted product
        Product product = new Product();
        product.setProductName("Test Product");
        product.setPrice(1000);
        product.setImageUrl("test url");
        product.setDescription("test description");
        product.setStockAmount(10);

        // Mock created product
        Product createdProduct = new Product();
        createdProduct.setId(1L);
        createdProduct.setProductName("Test Product");
        createdProduct.setPrice(1000);
        createdProduct.setImageUrl("test url");
        createdProduct.setDescription("test description");
        createdProduct.setStockAmount(10);

        // Mock converted productResponseDto
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(1L);
        productResponseDto.setProductName("Test Product");
        productResponseDto.setPrice(1000);
        productResponseDto.setImageUrl("test url");
        productResponseDto.setDescription("test description");
        productResponseDto.setStockAmount(10);

        // Mock productConverter.productDtoToProduct
        when(productConverter.productDtoToProduct(any(ProductDto.class))).thenReturn(product);

        // Mock productService.registerProduct
        when(productService.registerProduct(any(Product.class))).thenReturn(createdProduct);

        // Mock productConverter.productToProductResponseDto
        when(productConverter.productToProductResponseDto(any(Product.class))).thenReturn(productResponseDto);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);
        ResultActions perform = mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.productName").value(productResponseDto.getProductName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void patchProduct() throws Exception {
        long id = 1L;

        ProductResponseDto productResponseDto = new ProductResponseDto(id, "update Product name", 9999, "update url", "update description", 99, 0,0,0,null);

        when(productConverter.productDtoToProduct(any(ProductDto.class))).thenReturn(new Product());
        when(productService.updateProduct(any(Long.class), any(Product.class))).thenReturn(new Product());
        when(productConverter.productToProductResponseDto(any(Product.class))).thenReturn(productResponseDto);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);
        ResultActions perform = mockMvc.perform(patch("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.productName").value(productResponseDto.getProductName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProduct() throws Exception {
        long id = 1L;

        Product product = new Product();
        product.setProductName("Test Product");
        product.setPrice(1000);
        product.setImageUrl("test url");
        product.setDescription("test description");
        product.setStockAmount(10);

        ProductResponseDto productResponseDto = new ProductResponseDto(id, "Test Product", 1000, "test url", "test description", 10, 0,0,0,null);


        when(productService.findProduct(any(Long.class))).thenReturn(new Product());
        when(productConverter.productToProductResponseDto(any(Product.class))).thenReturn(productResponseDto);

        String expectedResponse = objectMapper.writeValueAsString(productResponseDto);
        System.out.println("expectedResponse = " + expectedResponse);

        ResultActions perform = mockMvc.perform(get("/products/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(expectedResponse));

        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponseDto.getId()))
                .andExpect(jsonPath("$.productName").value(productResponseDto.getProductName()))
                .andExpect(jsonPath("$.price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.imageUrl").value(productResponseDto.getImageUrl()))
                .andExpect(jsonPath("$.description").value(productResponseDto.getDescription()))
                .andExpect(jsonPath("$.stockAmount").value(productResponseDto.getStockAmount()));
    }

    @Test
    void getProducts() throws Exception {

        Product product1 = new Product();
        product1.setProductName("product1");
        product1.setPrice(1);
        product1.setImageUrl("url1");
        product1.setDescription("description1");
        product1.setStockAmount(1);

        Product product2 = new Product();
        product2.setProductName("product2");
        product2.setPrice(2);
        product2.setImageUrl("url2");
        product2.setDescription("description2");
        product2.setStockAmount(2);

        List<Product> productList = Arrays.asList(
                product1, product2
        );

        ProductResponseDto productResponseDto1 = new ProductResponseDto(1L, "product1", 1, "url1", "description1", 1, 0,0,0,null);
        ProductResponseDto productResponseDto2 = new ProductResponseDto(2L, "product2", 2, "url2", "description2", 2, 0,0,0,null);

        List<ProductResponseDto> productResponseDtoList = Arrays.asList(
                productResponseDto1, productResponseDto2
        );


        when(productService.findProducts(anyInt())).thenReturn(productList);
        when(productConverter.productToProductResponseDto(productList.get(0))).thenReturn(productResponseDto1);
        when(productConverter.productToProductResponseDto(productList.get(1))).thenReturn(productResponseDto2);

        ResultActions actions = mockMvc.perform(get("/products")
                        .param("page", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        MvcResult result = actions.andReturn();
        String responseContent = result.getResponse().getContentAsString();
        List<ProductResponseDto> productResponseDto = objectMapper.readValue(responseContent, new TypeReference<List<ProductResponseDto>>() {});

        Assertions.assertThat(productResponseDto).isEqualTo(productResponseDtoList);
    }

    @Test
    void searchProduct() {
        String keyword = "find";

    }

    @Test
    void deleteProduct() throws Exception {
        long id = 1L;

        mockMvc.perform(delete("/products/{id}", id))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(id);
    }
}