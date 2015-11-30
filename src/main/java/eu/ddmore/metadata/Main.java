package eu.ddmore.metadata;

import eu.ddmore.metadata.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 10:51
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("metadatalib-spring-config.xml");
        ValidationReport validationReport = context.getBean(ValidationReportImpl.class);
        validationReport.generateValidationReport(new File("resources\\DDMODEL00000545.rdf"));
        System.out.print(validationReport.getValidationReport());
        //System.out.print(validationReport.generateValidationReports(new File("resources")));
        /*try {
            System.out.print(validationReport.generateValidationReport(new URL("http://wwwdev.ebi.ac.uk/biomodels/model-repository/model/download/DDMODEL00000413.1?filename=Friberg_2009_Schizophrenia_Asenapine_PANSS_20140924_v5_Nonmem-validated.rdf")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
*/
    }
}
