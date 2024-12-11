package com.quangduy.productservice.Utils.Exception;

public class ProductException extends RuntimeException {
    public ProductException(String message) {
        super(message);
    }

    public static class DuplicateSkuException extends ProductException {
        public DuplicateSkuException(String message) {
            super(message);
        }
    }

    // Inner static class for ProductNotFoundException
    public static class ProductNotFoundException extends ProductException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidUpdateException extends RuntimeException {
        public InvalidUpdateException(String message) {
            super(message);
        }
    }

    public static class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }
}