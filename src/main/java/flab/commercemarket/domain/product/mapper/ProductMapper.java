package flab.commercemarket.domain.product.mapper;

import flab.commercemarket.domain.product.vo.Product;
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

    List<Product> findAll(int offset, int limit);

    int countProduct();

    List<Product> searchProduct(String keyword, int offset, int limit);

    int searchProductCountByKeyword(String keyword);

    void deleteProduct(long id);

    void updateLikeCount(Product product);
}
