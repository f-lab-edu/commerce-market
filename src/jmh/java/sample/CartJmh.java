package sample;

import flab.commercemarket.domain.cart.vo.Cart;
import flab.commercemarket.domain.product.vo.Product;
import flab.commercemarket.domain.user.vo.User;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(value = 1, warmups = 2)
public class CartJmh {
    List<Cart> cartList = new ArrayList<>();

    @Benchmark
    public void forLoopBenchmark(Blackhole blackhole) {
        blackhole.consume(forLoop(1));
    }

    @Benchmark
    public void streamBenchmark(Blackhole blackhole) {
        blackhole.consume(stream(1));
    }

    @Benchmark
    public void parallelStreamBenchmark(Blackhole blackhole) {
        blackhole.consume(parallelStream(1));
    }

    public int forLoop(long userId) {
        List<Cart> carts = findAllByUserId(userId);

        int sum = 0;

        for (Cart cart : carts) {
            int quantity = cart.getQuantity();
            long productId = cart.getProductId();
            int price = getProduct(productId).getPrice();
            sum += quantity * price;
        }
        return sum;
    }

    public int stream(long userId) {
        List<Cart> carts = findAllByUserId(userId);

        return carts.stream()
                .mapToInt(cart -> {
                    int quantity = cart.getQuantity();
                    long productId = cart.getProductId();
                    int price = getProduct(productId).getPrice();
                    return quantity * price;
                }).sum();
    }

    public int parallelStream(long userId) {
        List<Cart> carts = findAllByUserId(userId);

        return carts.parallelStream()
                .mapToInt(cart -> {
                    int quantity = cart.getQuantity();
                    long productId = cart.getProductId();
                    int price = getProduct(productId).getPrice();
                    return quantity * price;
                }).sum();
    }

    public List<Cart> findAllByUserId(long userId) {
        User user = User.builder().id(userId).build();
        for (int i = 1; i <= 100; i++) {
            Product product = Product.builder().id((long) i).price(i * 10000).build();
            cartList.add(Cart.builder().user(user).product(product).quantity(i).build());
        }
        return cartList;
    }

    public Product getProduct(long productId) {
        return cartList.get((int) (productId - 1)).getProduct();
    }
}
