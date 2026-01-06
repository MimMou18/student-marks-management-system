package com.sdlc.pro.smms.exception;

import jakarta.persistence.PersistenceException;
import lombok.Getter;

@Getter
public class DuplicateFieldException extends PersistenceException {
    public DuplicateFieldException(String message) {
        super(message);
    }
}
