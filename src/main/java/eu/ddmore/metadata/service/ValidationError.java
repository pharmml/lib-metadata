package eu.ddmore.metadata.service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 05/10/2015
 *         Time: 11:03
 */
public class ValidationError {

    private final ValidationErrorStatus errorStatus;
    private final String message;

    public ValidationError(ValidationErrorStatus errorStatus, String message) {
        this.errorStatus = errorStatus;
        this.message = message;
    }

    public ValidationErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public String getMessage() {
        return message;
    }
}
