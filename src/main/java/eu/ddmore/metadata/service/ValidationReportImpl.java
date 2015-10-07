package eu.ddmore.metadata.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 26/03/2015
 *         Time: 10:24
 */
public class ValidationReportImpl implements ValidationReport {

    private MetadataValidator metadataValidator;

    public ValidationReportImpl(MetadataValidator metadataValidator) {
        this.metadataValidator = metadataValidator;
    }

    public String generateValidationReport(File file){
        metadataValidator.ddmoreCertified(file.toURI().toString());
        return validationReport(metadataValidator.getValidationHandler(),file.getName());
    }

    public String generateValidationReports(File directory){
        StringBuffer stringBuffer = new StringBuffer();
        if(directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                metadataValidator.ddmoreCertified(file.toURI().toString());
                stringBuffer.append(validationReport(metadataValidator.getValidationHandler(), file.getName()));
                stringBuffer.append("\n\n");

            }
        }
        return stringBuffer.toString();
    }

    public String generateValidationReport(URL url){
        metadataValidator.ddmoreCertified(url.toString());
        return validationReport(metadataValidator.getValidationHandler(),url.toString());
    }

    private String validationReport(ValidationHandler validationHandler, String fileName){
        StringBuffer validationReport = new StringBuffer("Validation Report: " + fileName);
        validationReport.append("\n");
        boolean valid = true;
        for(ValidationError validationError: validationHandler.getValidationList()){
            if(ValidationErrorStatus.ERROR.equals(validationError.getErrorStatus())){
                valid = false;
            }
            validationReport.append(validationError.getErrorStatus()+ ": " + validationError.getMessage());
            validationReport.append("\n");
        }

        if(valid){
            validationReport.append("This is a valid DDMoRe certified metadata document");
        }
        else {
            validationReport.append("This is not valid DDMoRe certified metadata document. Please provide the fields marked as error.");
        }
        return validationReport.toString();
    }
}
