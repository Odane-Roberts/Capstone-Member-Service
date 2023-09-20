package dev.odane.memberservice.exception;

public class BagIsEmptyException extends RuntimeException {


    public BagIsEmptyException(String msg) {
        super(msg);
    }
}
