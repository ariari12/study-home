package hello.core.java.order;

public interface OrderService {
    Order createOrder(Long memberId, String itemName, int itemPrice);
}
