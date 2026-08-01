package microarch.delivery;

import lombok.extern.slf4j.Slf4j;
import microarch.delivery.adapters.in.http.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleValidation(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        var message = fieldError == null ? "Некорректные параметры запроса"
                : "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage());
        return ResponseEntity.badRequest().body(new Error(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Error> handleUnexpected(Exception ex) {
        log.error("Unexpected error while handling HTTP request", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Внутренняя ошибка сервиса"));
    }
}
