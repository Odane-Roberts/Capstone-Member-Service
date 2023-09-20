package dev.odane.memberservice.exception;


public class BookNotFoundBookException extends RuntimeException {
    public BookNotFoundBookException(String message) {
        super(message);
    }
}
