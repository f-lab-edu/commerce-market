package flab.commercemarket.cart.service;

import flab.commercemarket.cart.domain.Cart;
import flab.commercemarket.cart.mapper.CartMapper;
import flab.commercemarket.exception.DataNotFoundException;
import flab.commercemarket.exception.DuplicateDataException;
import flab.commercemarket.exception.ForbiddenException;
import flab.commercemarket.helper.AuthorizationHelper;
import flab.commercemarket.product.service.ProductService;
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
        validateUserAndProductExistence(data);
        checkDuplicateCartItem(userId, data.getProductId());
        cartMapper.createCart(data);

        log.info("Create cart. {}", data);
        return data;
    }

    public Cart updateCart(Cart data, long cartId, long userId) {
        log.info("Start updateCart");

        Cart foundCart = verifiedCart(cartId);
        authorizationHelper.checkUserAuthorization(foundCart.getUserId(), userId);

        // userID와 productId는 수정되면 안된다. -> foundCart 값을 그대로 입력
        foundCart.setUserId(foundCart.getUserId());
        foundCart.setProductId(foundCart.getProductId());
        foundCart.setQuantity(data.getQuantity());
        cartMapper.updateCart(foundCart);

        log.info("Update cart. {}", foundCart);
        return foundCart;
    }

    public List<Cart> getCarts(long userId) {
        log.info("Start getCarts");

        // todo 테이블의 모든 항목을 쿼리해온다. -> 성능이슈 예상
        // todo 페이징 처리 등 개선방법 생각해보기
        log.info("GetCarts, userId = {}", userId);
        return cartMapper.findAll(userId);
    }


    public void deleteCart(long cartId, long userId) {
        log.info("Start deleteCart");

        Cart cart = verifiedCart(cartId);
        if (cart.getUserId() != userId) {
            throw new ForbiddenException("유저 권한정보가 일치하지 않음");
        }

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

    private Cart verifiedCart(long cartId) {
        Optional<Cart> optionalCart = cartMapper.findById(cartId);
        return optionalCart.orElseThrow(() -> {
            log.info("cartId = {}", cartId);
            return new DataNotFoundException("조회한 장바구니 정보가 없음");
        });
    }

    private void validateUserAndProductExistence(Cart data) {
        productService.getVerifiedProduct(data.getProductId());
    }

    private void checkDuplicateCartItem(long userId, long productId) {
        if (cartMapper.checkCartExistence(userId, productId)) {
            log.info("userId = {}, productId = {}", userId, productId);
            throw new DuplicateDataException("이미 장바구니에 담긴 상품");
        }
    }
}
