package com.example.librarymanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadGatewayException extends RuntimeException {

    private Object[] params;

    public BadGatewayException(String message) {
        super(message);
    }

    public BadGatewayException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}
