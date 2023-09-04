package flab.commercemarket.domain.product.repository;

import flab.commercemarket.domain.product.vo.Product;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    List<Product> findByKeyword(String keyword, Pageable pageable);
    long countSearchProductByKeyword(String keyword);

}
