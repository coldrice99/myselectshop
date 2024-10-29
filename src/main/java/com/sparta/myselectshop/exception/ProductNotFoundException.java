package com.sparta.myselectshop.exception;
// 커스텀 Exception
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message); // 부모쪽으로 메세지 전달
    }
}
