package com.example.librarymanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForbiddenException extends RuntimeException {

    private Object[] params;

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}