package com.sdlc.pro.smms.util;

import com.sdlc.pro.smms.exception.DuplicateFieldException;
import jakarta.persistence.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;

public final class ExceptionTranslator {
    public static PersistenceException handleDuplicateConstraint(DataIntegrityViolationException exception) {
        Throwable cause = exception.getCause();
        if (cause instanceof org.hibernate.exception.ConstraintViolationException cve) {
            String message = cve.getErrorMessage();
            int index = message.indexOf("Detail:");
            if (index != -1) {
                return new DuplicateFieldException("[" + message.substring(index + 11));
            }
        }

        return new PersistenceException("Unexpected database persistence exception", exception);
    }

}
