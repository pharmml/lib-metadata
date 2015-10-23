package eu.ddmore.metadata.service;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public void generateValidationReport(File file, String submissionId) throws ValidationException {
        metadataValidator.readModel(file.toURI().toString());
        metadataValidator.validate(submissionId);

    }

/*    public String generateValidationReports(File directory){
        StringBuffer stringBuffer = new StringBuffer();
        if(directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                metadataValidator.validate(file.toURI().toString());
                stringBuffer.append(validationReport(metadataValidator.getValidationHandler(), file.getName()));
                stringBuffer.append("\n\n");

            }
        }
        return stringBuffer.toString();
    }*/

    public void generateValidationReport(URL url,String submissionId) throws ValidationException {
        metadataValidator.readModel(url.toString());
        metadataValidator.validate(submissionId);

    }

    public ArrayList<String> getValidationStatementList(){
        ArrayList<String> validationReport = new ArrayList<String>();

        boolean valid = true;
        for(ValidationError validationError: metadataValidator.getValidationHandler().getValidationList()){
            if(ValidationErrorStatus.ERROR.equals(validationError.getErrorStatus())){
                valid = false;
            }
            validationReport.add(validationError.getErrorStatus() + ": " + validationError.getMessage());
        }

        if(valid){
            validationReport.add(0,"STATUS: This is a valid DDMoRe certified metadata document.");
        }
        else {
            validationReport.add(0,"STATUS: This is not valid DDMoRe certified metadata document. Please provide the fields marked as error.");
        }
        return validationReport;
    }

    public String getValidationReport(){
        ArrayList<String> validationStatementList =  getValidationStatementList();
        StringBuffer validationReport = new StringBuffer();
        for(String validationStatement: validationStatementList){
            validationReport.append(validationStatement).append("     ");
        }
        return validationReport.toString();
    }

    public MetadataValidator getMetadataValidator() {
        return metadataValidator;
    }
}
