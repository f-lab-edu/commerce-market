package flab.commercemarket.product.controller;

import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;
import flab.commercemarket.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductResponseDto postProduct(@RequestBody ProductDto productDto) {
        Product product = productDto.toProduct();
        Product createdProduct = productService.registerProduct(product);
        return createdProduct.toProductResponseDto();
    }

    @PatchMapping("/{productId}")
    public ProductResponseDto patchProduct(@PathVariable("productId") long productId,
                                           @RequestBody ProductDto productDto) {
        Product product = productDto.toProduct();
        Product updateProduct = productService.updateProduct(productId, product);

        return updateProduct.toProductResponseDto();
    }

    @GetMapping("/{productId}")
    public ProductResponseDto getProduct(@PathVariable("productId") long productId) {
        Product product = productService.findProduct(productId);
        return product.toProductResponseDto();
    }

    @GetMapping
    public List<ProductResponseDto> getProducts(@RequestParam int page) {
        List<Product> products = productService.findProducts(page);

        return products.stream()
                .map(Product::toProductResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ProductResponseDto> searchProduct(@RequestParam String keyword) {
        List<Product> products = productService.searchProduct(keyword);
        return products.stream()
                .map(Product::toProductResponseDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable("productId") long productId) {
        productService.deleteProduct(productId);
    }

    @PostMapping("/{productId}/likes")
    public void postLike(@PathVariable("productId") long productId) {
        productService.updateLikeCount(productId);
    }
}
