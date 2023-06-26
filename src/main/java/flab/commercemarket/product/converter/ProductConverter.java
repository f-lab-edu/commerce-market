package flab.commercemarket.product.converter;

import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;

public interface ProductConverter {
    Product productDtoToProduct(ProductDto productDto);
    ProductResponseDto productToProductResponseDto(Product product);
}
