package br.com.blog.config.exceptions;

import br.com.blog.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String campo = ex.getBindingResult().getFieldError().getField();
        String mensagem = ex.getBindingResult().getFieldError().getDefaultMessage();

        ApiResponse response = new ApiResponse()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Campo '" + campo + "' " + mensagem);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        logger.error("Erro de argumento inválido", ex);
        ApiResponse response = new ApiResponse()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("O ID informado não pode ser nulo ou em branco");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
