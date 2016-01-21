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
    private final String value;

    public ValidationError(ValidationErrorStatus errorStatus, String qualifier, String value) {
        this.errorStatus = errorStatus;
        this.qualifier = qualifier;
        this.value = value;
    }

    public ValidationError(ValidationErrorStatus errorStatus, String qualifier) {
        this(errorStatus,qualifier,"");
    }

    public ValidationErrorStatus getErrorStatus() {
        return errorStatus;
    }

    public String getValue() {
        return value;
    }

    public String getQualifier() {
        return qualifier;
    }
}
