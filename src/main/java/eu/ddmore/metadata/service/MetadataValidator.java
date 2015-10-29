package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 14:37
 */
public interface MetadataValidator {
    public void readModel(String url);
    public void validate(String submissionId) throws ValidationException;
    public ValidationHandler getValidationHandler();
    public ValidationStatus getValidationErrorStatus();
    public void setModel(Model model);
}
