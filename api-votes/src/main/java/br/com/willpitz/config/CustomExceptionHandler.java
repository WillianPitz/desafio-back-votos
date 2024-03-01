package br.com.willpitz.config;

import br.com.willpitz.exception.BadRequestException;
import br.com.willpitz.exception.NotFoundException;
import feign.FeignException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleFeignException(FeignException ex) {
        return ErrorResponse.builder()
            .error("Ocorreu um erro ao realizar a operação")
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundException ex) {
        return ErrorResponse.builder()
            .error("Ocorreu um erro ao realizar a operação")
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleBadRequestException(BadRequestException ex) {
        return ErrorResponse.builder()
            .error("Ocorreu um erro ao realizar a operação")
            .message(ex.getMessage())
            .build();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleWebExchangeBindException(WebExchangeBindException ex) {
        var errorMessage = ex.getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        var concatenatedErrorMessage = String.join(", ", errorMessage);

        return ErrorResponse.builder()
            .error("Ocorreu um erro ao realizar a operação")
            .message(concatenatedErrorMessage)
            .build();
    }
}
