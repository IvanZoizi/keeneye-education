package keenay.education.exception;

import keenay.education.exception.errors.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createMessage(Throwable ex) {
        Map<String, Object> body = new HashMap<>();

        String message = ex.getMessage();
        if (message == null || message.isEmpty()) {
            message = ex.getClass().getSimpleName() + " occurred";
        }

        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        body.put("exception", ex.getClass().getSimpleName());

        ex.printStackTrace();

        return body;
    }

    @ExceptionHandler({AnimalIsNotSupported.class, PetsNotFoundException.class,
            SkillNotFoundException.class, TicketHasNotBeenCreatedException.class,
            EntityNotFoundException.class, TaskNotFoundException.class,
            AdvertisementNotFoundException.class, AdvertisementResponseNotFoundException.class,
            ChatNotFoundException.class, UserIsNotFoundException.class})
    public ResponseEntity<Object> handleAnimalIsNotSupportedException(Exception ex) {
        Map<String, Object> body = createMessage(ex);
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Object> handleAuthorizationExceptionException(Exception ex) {
        Map<String, Object> body = createMessage(ex);
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({InternalException.class, NoMailFoundException.class, Exception.class})
    public ResponseEntity<Object> handleInternalException(Exception ex) {
        Map<String, Object> body = createMessage(ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class, AuthenticationException.class, TaskBusyException.class,
            AdvertisementResponseBusyException.class, ChatAlreadyCreated.class,
            ImageDeleteException.class, ImageUploadException.class})
    public ResponseEntity<Object> handleRuntimeException(Exception ex) {
        Map<String, Object> body = createMessage(ex);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}