package microarch.delivery.adapters.in.http;

import microarch.delivery.adapters.in.http.model.Error;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;


public final class ErrorResponseMapper {

    /** Коды доменных ошибок, которые трактуются как конфликт (409), а не как некорректный запрос (400). */
    private static final Set<String> CONFLICT_CODES = Set.of("404", "record.not.found");

    private ErrorResponseMapper() {
    }

    @SuppressWarnings("unchecked")
    public static <T> ResponseEntity<T> toResponse(libs.errs.Error error) {
        var status = CONFLICT_CODES.contains(error.getCode()) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        var body = new Error(status.value(), error.getMessage());
        return (ResponseEntity<T>) ResponseEntity.status(status).body(body);
    }
}
