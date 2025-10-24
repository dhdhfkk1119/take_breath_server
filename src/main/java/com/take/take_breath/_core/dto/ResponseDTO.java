package com.take.take_breath._core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDTO<T> {
    private String message;
    private T data;

    public static <T> ResponseDTO<T> of(String message, T data) {
        return new ResponseDTO<>(message, data);
    }
}