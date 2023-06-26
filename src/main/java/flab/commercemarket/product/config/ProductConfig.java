package flab.commercemarket.product.config;

import flab.commercemarket.product.converter.ProductConverter;
import flab.commercemarket.product.converter.ProductConverterImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ProductConfig {

    @Bean
    public ProductConverter productConverter() {
        return new ProductConverterImpl();
    }

}
