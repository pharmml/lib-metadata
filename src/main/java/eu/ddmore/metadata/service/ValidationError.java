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
    private final String qualifier;
    private final String message;

    public ValidationError(ValidationErrorStatus errorStatus, String qualifier, String message) {
        this.errorStatus = errorStatus;
        this.qualifier = qualifier;
        this.message = message;
    }

    public ValidationErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public String getMessage() {
        return message;
    }

    public String getQualifier() {
        return qualifier;
    }
}
