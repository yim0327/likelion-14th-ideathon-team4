package com.inuni.ideathon.global.exception.code;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {
    String getCode();
    String getMessage();
    HttpStatus getStatus();
}
