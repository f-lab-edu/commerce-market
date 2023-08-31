package flab.commercemarket.domain.product.repository;

import flab.commercemarket.domain.product.vo.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(long productId);

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    Page<Product> findByKeyword(Pageable pageable, String keyword);

    @Modifying
    @Query("UPDATE Product p SET p.likeCount= :likeCount WHERE p.id = :productId")
    void updateLikeCount(long productId, int likeCount);
}
