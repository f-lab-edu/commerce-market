package flab.commercemarket.product.service;

import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.product.domain.Product;
import flab.commercemarket.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public Product registerProduct(Product product) {
        // todo 인증로직 구현 후 product에 sellerID를 묶어주어야함.
        productMapper.insertProduct(product);
        return product;
    }

    public Product updateProduct(long id, Product data) {
        // todo 요청으로 넘어오는 data에 null 값이 없는지 체크하는 로직이 필요합니다.

        Product foundProduct = getVerifiedProduct(id);
        foundProduct.setName(data.getName());
        foundProduct.setPrice(data.getPrice());
        foundProduct.setImageUrl(data.getImageUrl());
        foundProduct.setDescription(data.getDescription());
        foundProduct.setStockAmount(data.getStockAmount());

        productMapper.updateProduct(foundProduct);
        return foundProduct;
    }


    public Product findProduct(long id) {
        return getVerifiedProduct(id);
    }

    public List<Product> findProducts(int page) {
        int size = 10;
        int offset = (page - 1) * size;

        return productMapper.findAll(offset, size);
    }

    public List<Product> searchProduct(String keyword) {
        return productMapper.searchProduct(keyword);
    }

    public void deleteProduct(long id) {
        getVerifiedProduct(id);
        productMapper.deleteProduct(id);
    }

    private Product getVerifiedProduct(Long id) {
        Optional<Product> optionalProduct = productMapper.findById(id);
        return optionalProduct.orElseThrow(() -> new DataNotFoundException("조회한 상품 정보가 없음"));
    }
}
