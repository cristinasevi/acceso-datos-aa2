package acceso.datos.aa2.movies.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class ErrorResponse {
    private int code;
    private String title;
    private String message;
    private Map<String, String> errors;

    public static ErrorResponse generalError(int code, String title, String message) {
        return new ErrorResponse(code, title, message, new HashMap<>());
    }

    public static ErrorResponse notFound(String message) {
        return new ErrorResponse(404, "not-found", message, new HashMap<>());
    }

    public static ErrorResponse validationError(Map<String, String> errors) {
        return new ErrorResponse(400, "bad-request", "Validation error", errors);
    }

    public static ErrorResponse conflict(String message) {
        return new ErrorResponse(409, "conflict", message, new HashMap<>());
    }

    public static ErrorResponse internalServerError(String message) {
        return new ErrorResponse(500, "internal-server-error", message, new HashMap<>());
    }
}
