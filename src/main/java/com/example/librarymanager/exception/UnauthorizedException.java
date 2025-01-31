package com.example.librarymanager.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UnauthorizedException extends RuntimeException {

    private Object[] params;

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}