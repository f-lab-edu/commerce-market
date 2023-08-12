package flab.commercemarket.domain.cart;

import flab.commercemarket.common.exception.DataNotFoundException;
import flab.commercemarket.common.exception.DuplicateDataException;
import flab.commercemarket.common.helper.AuthorizationHelper;
import flab.commercemarket.domain.cart.mapper.CartMapper;
import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartMapper cartMapper;
    private final ProductService productService;
    private final AuthorizationHelper authorizationHelper;

    public Cart registerCart(Cart data, long userId) {
        log.info("Start registerCart");

        authorizationHelper.checkUserAuthorization(data.getUserId(), userId);
        checkValidProduct(data.getProductId());
        checkDuplicateCartItem(userId, data.getProductId());

        cartMapper.createCart(data);
        log.info("Create cart. {}", data);
        return data;
    }

    public Cart updateCart(Cart data, long cartId, long userId) {
        log.info("Start updateCart");

        Cart foundCart = getVerifiedCart(cartId);
        authorizationHelper.checkUserAuthorization(foundCart.getUserId(), userId);

        // userID와 productId는 수정되면 안된다.
        foundCart.setUserId(foundCart.getUserId());
        foundCart.setProductId(foundCart.getProductId());
        foundCart.setQuantity(data.getQuantity());
        cartMapper.updateCart(foundCart);

        log.info("Update cart. {}", foundCart);
        return foundCart;
    }

    public List<Cart> getCarts(long userId) {
        log.info("Start getCarts");

        // todo 페이지네이션 적용해야 합니다.
        log.info("GetCarts, userId = {}", userId);
        return cartMapper.findAll(userId);
    }

    public void deleteCart(long cartId, long userId) {
        log.info("Start deleteCart");

        Cart cart = getVerifiedCart(cartId);
        authorizationHelper.checkUserAuthorization(cart.getUserId(), userId);

        cartMapper.deleteCart(cartId);
        log.info("Delete Cart. cartId = {}", cartId);
    }

    public int calculateTotalPrice(long userId) {
        log.info("Start calculateTotalPrice");

        List<Cart> carts = getCarts(userId);

        int sum = 0;
        // 성능이슈 예상지점
        // TODO for루프, 순차스트림, 병렬스트림 성능비교 -> JMH 사용
        // TODO findProduct -> 상품 리스트 하나당 쿼리 하나가 나가는 로직 개선해야함
        for (Cart cart : carts) {
            int quantity = cart.getQuantity();
            long productId = cart.getProductId();
            int price = productService.findProduct(productId).getPrice();
            sum += quantity * price;
        }

        log.info("Calculate cart's total price");
        return sum;
    }

    private Cart getVerifiedCart(long cartId) {
        Optional<Cart> optionalCart = cartMapper.findById(cartId);
        return optionalCart.orElseThrow(() -> {
            log.info("cartId = {}", cartId);
            return new DataNotFoundException("조회한 장바구니 정보가 없음");
        });
    }

    private void checkValidProduct(long productId) {
        log.info("Start checkValidProduct.");

        if (!cartMapper.isExistentProduct(productId)) {
            log.info("productId = {}", productId);
            throw new DataNotFoundException("존재하지 않는 상품");
        }
    }

    private void checkDuplicateCartItem(long userId, long productId) {
        log.info("Start checkDuplicateCartItem.");

        if (cartMapper.isAlreadyExistentProductInUserCart(userId, productId)) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 장바구니에 담긴 상품");
        }
    }

}
