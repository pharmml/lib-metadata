package eu.ddmore.metadata.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 26/03/2015
 *         Time: 10:59
 */
public interface ValidationReport {
    void generateValidationReport(File file, String submissionId) throws ValidationException;
    /*public void generateValidationReports(File directory)*/;
    void generateValidationReport(URL url, String submissionId) throws ValidationException;
    ArrayList<String> getValidationStatementList();
    String getValidationReport();
    MetadataValidator getMetadataValidator();
}
