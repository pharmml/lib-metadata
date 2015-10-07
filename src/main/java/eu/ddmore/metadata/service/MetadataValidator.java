package eu.ddmore.metadata.service;

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
    public boolean ddmoreCertified(String file);
    public ValidationHandler getValidationHandler();
}
