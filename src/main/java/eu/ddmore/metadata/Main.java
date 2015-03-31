package eu.ddmore.metadata;

import eu.ddmore.metadata.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 10:51
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        ValidationReport validationReport = context.getBean(ValidationReportImpl.class);
        //System.out.print(validationReport.generateValidationReport(new File("resources\\example2.xml")));
        //System.out.print(validationReport.generateValidationReports(new File("resources")));
        try {
            System.out.print(validationReport.generateValidationReport(new URL("http://wwwdev.ebi.ac.uk/biomodels/model-repository/model/download/DDMODEL00000413.1?filename=Friberg_2009_Schizophrenia_Asenapine_PANSS_20140924_v5_Nonmem-validated.rdf")));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
}
