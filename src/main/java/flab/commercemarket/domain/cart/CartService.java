package flab.commercemarket.domain.cart;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.exception.ForbiddenException;
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

    @Transactional
    public Cart registerCart(CartDto data, String email) {
        log.info("Start registerCart");

        User foundUser = userService.getUserByEmail(email);
        Product foundProduct = productService.getProductById(data.getProductId());

        checkDuplicateCartItem(foundUser.getId(), data.getProductId());

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
    public Cart updateCart(CartDto data, long cartId, String email) {
        log.info("Start updateCart");

        Cart foundCart = getCart(cartId);
        User foundUser = userService.getUserByEmail(email);
        checkUserAuthorization(foundCart.getUserId(), foundUser.getId());

        foundCart.setQuantity(data.getQuantity());

        log.info("Update cart. {}", foundCart);
        return foundCart;
    }

    @Transactional(readOnly = true)
    public List<Cart> findCarts(String email, int page, int size) {
        log.info("Start getCarts. email = {}", email);
        User foundUser = userService.getUserByEmail(email);

        Pageable pageable = PageRequest.of(page - 1, size);
        return cartRepository.findCartByUserId(foundUser.getId(), pageable);
    }

    @Transactional(readOnly = true)
    public long countCartByUserEmail(String email) {
        return cartRepository.countCartByEmail(email);
    }

    @Transactional
    public void deleteCart(String email, long cartId) {
        log.info("Start deleteCart");

        Cart foundCart = getCart(cartId);
        User foundUser = userService.getUserByEmail(email);
        checkUserAuthorization(foundCart.getUserId(), foundUser.getId());

        cartRepository.delete(foundCart);
        log.info("Delete Cart. cartId = {}", cartId);
    }

    @Transactional(readOnly = true)
    public int calculateTotalPrice(String email) {
        log.info("Start calculateTotalPrice. email: {}", email);
        User foundUser = userService.getUserByEmail(email);
        List<Cart> carts = cartRepository.findAllByUserId(foundUser.getId());

        return carts.parallelStream()
                .mapToInt(cart -> {
                    int quantity = cart.getQuantity();
                    long productId = cart.getProductId();
                    int price = productService.getProductById(productId).getPrice();
                    return quantity * price;
                }).sum();
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

    private void checkUserAuthorization(long ownerUserId, long loginUserId) {
        if (ownerUserId != loginUserId) {
            log.info("dataUserId = {}, loginUserId = {}", ownerUserId, loginUserId);
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }
    }
}
