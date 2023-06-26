package flab.commercemarket.product.mapper;

import flab.commercemarket.product.domain.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface ProductMapper {

    void insertProduct(Product product);

    void updateProduct(Product product);

    Optional<Product> findById(long id);

    List<Product> findAll(int offset, int size);

    List<Product> searchProduct(String keyword);

    void deleteProduct(long id);
}
