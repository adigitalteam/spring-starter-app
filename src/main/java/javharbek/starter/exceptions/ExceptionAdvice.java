package javharbek.starter.exceptions;

import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionAdvice {



    @ExceptionHandler(value = {AppException.class})
    protected ResponseEntity<?> AppException(AppException exception, HttpServletRequest httpServletRequest) {

        final HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        final String message = exception.getMessage();
        final String requestURI = httpServletRequest.getRequestURI();
        final Date timestamp = new Date(System.currentTimeMillis());
        Map<String, Object> responseBody = new HashMap<>();

        responseBody.put("timestamp", timestamp.getTime());
        responseBody.put("status", status.value());
        responseBody.put("errorCode", exception.code);
        responseBody.put("errorContent", exception.getMessage());
        responseBody.put("errorData", exception.errorData);
        responseBody.put("error", status.getReasonPhrase());
        responseBody.put("message", message);
        responseBody.put("path", requestURI);
        responseBody.put("stackTrace", ExceptionUtils.getStackTrace(exception));

        return ResponseEntity.status(status).body(responseBody);
    }
}
