package gbas.gtbch.web.controlleradvice;

import gbas.gtbch.web.controlleradvice.annotations.DuplicateKeyExceptionHandler;
import gbas.gtbch.web.response.Response;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

/**
 * <p>custom controller's DuplicatKeyException handler</p>
 * <p></p>
 * <p>@RestController</p>
 * <p>@DuplicateKeyExceptionHandler</p>
 * <p>public class ExampleRestController {</p>
 * <p>    ...</p>
 * <p>}</p>
 * see {@link DuplicateKeyExceptionHandler}, {@link ControllerAdvice}
 */
@ControllerAdvice(annotations = DuplicateKeyExceptionHandler.class)
public class DuplicateKeyAdvice {

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Response> handleException(DuplicateKeyException e) {
        String message = String.format("%s %s", LocalDateTime.now(), e.getMessage());
        Response response = new Response(message);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}