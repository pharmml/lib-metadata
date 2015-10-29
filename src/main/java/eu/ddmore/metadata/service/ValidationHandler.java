package eu.ddmore.metadata.service;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 02/10/2015
 *         Time: 15:32
 */
public class ValidationHandler{

    private ArrayList<ValidationError> validationList = new ArrayList<ValidationError>();

    public void addValidationError(ValidationError validationError){
        validationList.add(validationError);
    }
    public ArrayList<ValidationError> getValidationList() {
        return validationList;
    }
}
