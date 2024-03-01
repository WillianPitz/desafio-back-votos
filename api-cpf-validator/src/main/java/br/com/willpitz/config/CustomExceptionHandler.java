package br.com.willpitz.config;

import br.com.willpitz.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleBadRequestException(NotFoundException ex) {
        return ErrorResponse.builder()
            .error("Ocorreu um erro ao realizar a operação")
            .message(ex.getMessage())
            .build();
    }
}
