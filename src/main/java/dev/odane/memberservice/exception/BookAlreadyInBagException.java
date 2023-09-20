package dev.odane.memberservice.exception;

public class BookAlreadyInBagException extends RuntimeException {
    public BookAlreadyInBagException(String msg) {
        super(msg);
    }
}
