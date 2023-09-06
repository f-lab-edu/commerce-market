package flab.commercemarket.controller.product;

import flab.commercemarket.common.responsedto.PageResponseDto;
import flab.commercemarket.controller.product.dto.ProductDto;
import flab.commercemarket.controller.product.dto.ProductResponseDto;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
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
                                           @RequestBody @Validated ProductDto productDto) {
        Product product = productDto.toProduct();
        Product updateProduct = productService.updateProduct(productId, product);

        return updateProduct.toProductResponseDto();
    }

    @GetMapping("/{productId}")
    public ProductResponseDto getProduct(@PathVariable("productId") long productId) {
        Product product = productService.getProduct(productId);
        return product.toProductResponseDto();
    }

    @GetMapping
    public PageResponseDto<ProductResponseDto> getProducts(@RequestParam int page, @RequestParam int size) {
        Page<Product> productPage = productService.findProducts(page, size);

        List<ProductResponseDto> productResponseDto = productPage.stream()
                .map(Product::toProductResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<ProductResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(productPage.getTotalElements())
                .content(productResponseDto)
                .build();
    }

    @GetMapping("/search")
    public PageResponseDto<ProductResponseDto> searchProduct(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
        List<Product> products = productService.searchProduct(keyword, page, size);
        long totalElements = productService.countSearchProductByKeyword(keyword);

        List<ProductResponseDto> productResponseDto = products.stream()
                .map(Product::toProductResponseDto)
                .collect(Collectors.toList());

        return PageResponseDto.<ProductResponseDto>builder()
                .page(page)
                .size(size)
                .totalElements(totalElements)
                .content(productResponseDto)
                .build();
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable("productId") long productId, @RequestParam long loginUserId) {
        productService.deleteProduct(productId, loginUserId);
    }

    @PostMapping("/{productId}/likes")
    public void postLike(@PathVariable("productId") long productId) {
        productService.updateLikeCount(productId);
    }
}
