package dashboard.pdp;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@SuppressWarnings("serial")
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class PolicyNameNotUniqueException extends RuntimeException {

    private String soep;

    public PolicyNameNotUniqueException() {
        this.soep = "empty";
    }

    public PolicyNameNotUniqueException(String message) {
        super(message);
        this.soep = message;
    }

    public String getSoep() {
        return soep;
    }

}