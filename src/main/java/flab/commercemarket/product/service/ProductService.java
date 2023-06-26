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
        Product findProduct = findVerifiedProduct(id);
        findProduct.setProductName(data.getProductName() == null ? findProduct.getProductName() : data.getProductName());
        findProduct.setPrice(data.getPrice() == 0 ? findProduct.getPrice() : data.getPrice());
        findProduct.setImageUrl(data.getImageUrl() == null ? findProduct.getImageUrl() : data.getImageUrl());
        findProduct.setDescription(data.getDescription() == null ? findProduct.getDescription() : data.getDescription());
        findProduct.setStockAmount(data.getStockAmount() == 0 ? findProduct.getStockAmount() : data.getStockAmount());

        productMapper.updateProduct(findProduct);
        return findProduct;
    }


    public Product findProduct(long id) {
        return findVerifiedProduct(id);
    }

    public List<Product> findProducts(int page) {
        int size = 10;
        int offset = (page-1)*size;

        return productMapper.findAll(offset, size);
    }

    public List<Product> searchProduct(String keyword) {
        return productMapper.searchProduct(keyword);
    }

    public void deleteProduct(long id) {
        findVerifiedProduct(id);
        productMapper.deleteProduct(id);
    }

    private Product findVerifiedProduct(Long id) {
        Optional<Product> optionalProduct = productMapper.findById(id);
        return optionalProduct.orElseThrow(() -> new DataNotFoundException("조회한 상품 정보가 없음"));
    }
}
