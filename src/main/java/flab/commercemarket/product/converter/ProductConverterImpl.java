package flab.commercemarket.product.converter;

import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.dto.ProductDto;
import flab.commercemarket.product.dto.ProductResponseDto;

public class ProductConverterImpl implements ProductConverter{

    @Override
    public Product productDtoToProduct(ProductDto productDto) {
        Product product = new Product();
        product.setProductName(productDto.getProductName());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        product.setDescription(productDto.getDescription());
        product.setStockAmount(productDto.getStockAmount());

        return product;
    }

    @Override
    public ProductResponseDto productToProductResponseDto(Product product) {
        ProductResponseDto productResponseDto = new ProductResponseDto();

        productResponseDto.setId(product.getId());
        productResponseDto.setProductName(product.getProductName());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setImageUrl(product.getImageUrl());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setStockAmount(product.getStockAmount());

        return productResponseDto;
    }
}
