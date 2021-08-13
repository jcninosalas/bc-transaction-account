package com.bootcamp.transactions.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseResponseDto<T> {
    final private HttpStatus status;
    final private String message;
    final private T body;

    public BaseResponseDto(HttpStatus status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    public BaseResponseDto(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        this.body = null;
    }
}
