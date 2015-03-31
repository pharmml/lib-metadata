package eu.ddmore.metadata;

import eu.ddmore.metadata.service.MetadataValidator;
import eu.ddmore.metadata.service.MetadataValidatorImpl;
import eu.ddmore.metadata.service.ValidationReport;
import eu.ddmore.metadata.service.ValidationReportImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 25/03/2015
 *         Time: 10:12
 */
public class CLI {
    public static void main (String [] args){
        System.out.println("Enter a file/folder/url path to generate a validation report: ");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String fileName ="";
        try {
            fileName = bufferedReader.readLine();
        } catch (IOException e) {
            System.out.println("IO error trying to read the file file/folder/url path.");
            System.exit(1);
        }



        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        ValidationReport validationReport = context.getBean(ValidationReportImpl.class);

        File file = new File(fileName);
        if(file.exists()) {
            if(file.isFile())
                System.out.println(validationReport.generateValidationReport(file));
            else if (file.isDirectory())
                System.out.println(validationReport.generateValidationReports(file));
        }else{
            try {
                URL url = new URL(fileName);
                System.out.println(validationReport.generateValidationReport(url));
            } catch (MalformedURLException e) {
                System.out.println("Invalid file/folder/url path");
            }
        }
    }

}
