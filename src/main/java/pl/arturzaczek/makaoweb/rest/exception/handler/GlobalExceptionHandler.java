package pl.arturzaczek.makaoweb.rest.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.arturzaczek.makaoweb.rest.dto.ApiErrorResponse;
import pl.arturzaczek.makaoweb.rest.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CardDeckException.class)
    protected ResponseEntity<ApiErrorResponse> handleCardDeckException(BaseGameException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(createErrorMessage(ex));
    }

    @ExceptionHandler(value = CardException.class)
    protected ResponseEntity<ApiErrorResponse> handleCardException(BaseGameException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(createErrorMessage(ex));
    }

    @ExceptionHandler(value = GameException.class)
    protected ResponseEntity<ApiErrorResponse> handleGameException(BaseGameException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorMessage(ex));
    }

    @ExceptionHandler(value = PlayerException.class)
    protected ResponseEntity<ApiErrorResponse> handlePlayerException(BaseGameException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(createErrorMessage(ex));
    }

    private ApiErrorResponse createErrorMessage(BaseGameException ex) {
        return ApiErrorResponse.builder()
                .message(ex.getMessage())
                .code(ex.getCode())
                .details(ex.getDetails())
                .build();
    }
}
