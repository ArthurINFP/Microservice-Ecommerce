package com.quangduy.cartservice.exception;

public class CartException extends RuntimeException{
    public CartException(String message) {super(message);}
    public static class UnauthorizedException extends CartException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}
