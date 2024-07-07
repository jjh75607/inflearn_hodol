package com.hodol.api.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class HodollogException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<String, String>();

    public HodollogException(String message) {
        super(message);
    }

    public HodollogException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String filedName, String message) {
        validation.put(filedName, message);
    }
}
