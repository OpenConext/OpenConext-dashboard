package dashboard.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestController
@RequestMapping("/error")
public class DefaultErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    @Autowired
    public DefaultErrorController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> result = this.errorAttributes.getErrorAttributes(
                webRequest,
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.EXCEPTION, ErrorAttributeOptions.Include.MESSAGE));

        Throwable error = this.errorAttributes.getError(webRequest);
        HttpStatus statusCode;

        if (error == null) {
            statusCode = result.containsKey("status") ? HttpStatus.valueOf((Integer) result.get("status")) : BAD_REQUEST;
        } else {
            //https://github.com/spring-projects/spring-boot/issues/3057
            ResponseStatus annotation = AnnotationUtils.getAnnotation(error.getClass(), ResponseStatus.class);
            statusCode = annotation != null ? annotation.value() : status(error);
        }
        result.remove("message");
        return new ResponseEntity<>(result, statusCode);
    }

    private HttpStatus status(Throwable throwable) {
        if (throwable instanceof AccessDeniedException) {
            return FORBIDDEN;
        }
        return BAD_REQUEST;
    }

}
