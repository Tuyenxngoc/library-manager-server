package com.example.librarymanager.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConflictException extends RuntimeException {

    private Object[] params;

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Object... params) {
        super(message);
        this.params = params;
    }

}