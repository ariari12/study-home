package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(String needMoreStock) {
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }

    public NotEnoughStockException() {
    }
}
