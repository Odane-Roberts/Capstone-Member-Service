package dev.odane.memberservice.exception;

public class BorrowedBookNotFoundException extends RuntimeException {
    public BorrowedBookNotFoundException(String msg) {
        super(msg);
    }
}
