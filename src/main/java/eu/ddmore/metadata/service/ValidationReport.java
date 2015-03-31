package eu.ddmore.metadata.service;

import java.io.File;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 26/03/2015
 *         Time: 10:59
 */
public interface ValidationReport {
    public String generateValidationReport(File file);
    public String generateValidationReports(File directory);
    public String generateValidationReport(URL url);
}
