package com.hodol.api.exception;

import lombok.Getter;

@Getter
public class InvalidRequest extends HodollogException {

    public static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String filedName, String message) {
        super(MESSAGE);
        addValidation(filedName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
