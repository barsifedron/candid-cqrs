package com.barsifedron.candid.cqrs.springboot;

import com.barsifedron.candid.cqrs.command.middleware.ValidatingCommandBusMiddleware;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WrongParametersAdvice {

    @ExceptionHandler(ValidatingCommandBusMiddleware.IllegalCommandException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public void handleWrongValue() {
    }
}
