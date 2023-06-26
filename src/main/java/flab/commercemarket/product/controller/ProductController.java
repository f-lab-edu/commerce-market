package flab.commercemarket.product.controller;

import flab.commercemarket.product.converter.ProductConverter;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;
import flab.commercemarket.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductConverter productConverter;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto postProduct(@RequestBody ProductDto productDto) {
        Product product = productConverter.productDtoToProduct(productDto);
        Product createdProduct = productService.registerProduct(product);
        return productConverter.productToProductResponseDto(createdProduct);
    }

    @PatchMapping("/{productId}")
    public ProductResponseDto patchProduct(@PathVariable("productId") long productId,
                                           @RequestBody ProductDto productDto) {
        Product product = productConverter.productDtoToProduct(productDto);
        Product updateProduct = productService.updateProduct(productId, product);

        return productConverter.productToProductResponseDto(updateProduct);
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public ProductResponseDto getProduct(@PathVariable("productId") long productId) {
        Product product = productService.findProduct(productId);
        return productConverter.productToProductResponseDto(product);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> getProducts(@RequestParam("page") int page) {
        List<Product> products = productService.findProducts(page);

        return products.stream()
                .map(productConverter::productToProductResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponseDto> searchProduct(@RequestParam("keyword") String keyword) {
        // todo List로 조회할 수 있게 변경
        List<Product> products = productService.searchProduct(keyword);
        return products.stream()
                .map(productConverter::productToProductResponseDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable("productId") long productId) {
        productService.deleteProduct(productId);
    }
}
