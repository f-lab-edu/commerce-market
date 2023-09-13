package flab.commercemarket.domain.cart;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.controller.cart.dto.CartDto;
import flab.commercemarket.domain.cart.repository.CartRepository;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.UserService;
import flab.commercemarket.domain.user.vo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductService productService;
    private final UserService userService;
    private final AuthorizationHelper authorizationHelper;

    @Transactional
    public Cart registerCart(CartDto data, long userId) {
        log.info("Start registerCart");

        authorizationHelper.checkUserAuthorization(data.getUserId(), userId);
        User foundUser = userService.getUserById(data.getUserId());
        Product foundProduct = productService.getProduct(data.getProductId());

        checkDuplicateCartItem(userId, data.getProductId());

        Cart cart = Cart.builder()
                .user(foundUser)
                .product(foundProduct)
                .quantity(data.getQuantity())
                .build();

        Cart createdCart = cartRepository.save(cart);

        log.info("Create cart. {}", createdCart);
        return createdCart;
    }

    @Transactional
    public Cart updateCart(CartDto data, long cartId, long userId) {
        log.info("Start updateCart");

        Cart foundCart = getCart(cartId);
        authorizationHelper.checkUserAuthorization(foundCart.getUserId(), userId);

        foundCart.setQuantity(data.getQuantity());
        Cart savedCart = cartRepository.save(foundCart);

        log.info("Update cart. {}", foundCart);
        return savedCart;
    }

    @Transactional(readOnly = true)
    public List<Cart> findCarts(long userId, int page, int size) {
        log.info("Start getCarts. userId = {}", userId);

        Pageable pageable = PageRequest.of(page - 1, size);
        return cartRepository.findCartByUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public long countCartByUserId(long userId) {
        return cartRepository.countCartByUserId(userId);
    }

    @Transactional
    public void deleteCart(long cartId, long userId) {
        log.info("Start deleteCart");

        Cart foundCart = getCart(cartId);
        authorizationHelper.checkUserAuthorization(foundCart.getUserId(), userId);

        cartRepository.delete(foundCart);
        log.info("Delete Cart. cartId = {}", cartId);
    }

    @Transactional(readOnly = true)
    public int calculateTotalPrice(long userId) {
        log.info("Start calculateTotalPrice. userId: {}", userId);

        List<Cart> carts = cartRepository.findAllByUserId(userId);

        int sum = 0;

        for (Cart cart : carts) {
            int quantity = cart.getQuantity();
            long productId = cart.getProductId();
            int price = productService.getProduct(productId).getPrice();
            sum += quantity * price;
        }

        log.info("Calculate cart's total price");
        return sum;
    }

    private Cart getCart(long cartId) {
        Optional<Cart> optionalCart = cartRepository.findById(cartId);
        return optionalCart.orElseThrow(() -> {
            log.info("cartId = {}", cartId);
            return new DataNotFoundException("조회한 장바구니 정보가 없음");
        });
    }

    private void checkDuplicateCartItem(long userId, long productId) {
        log.info("Start checkDuplicateCartItem.");

        if (cartRepository.isAlreadyExistentProductInUserCart(userId, productId)) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 장바구니에 담긴 상품");
        }
    }

}
