package com.example.librarymanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotFoundException extends RuntimeException {

    private Object[] params;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}
