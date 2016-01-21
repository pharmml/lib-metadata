package eu.ddmore.metadata;

import eu.ddmore.metadata.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;

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
        MetadataValidator metadataValidator = context.getBean(MetadataValidatorImpl.class);
        metadataValidator.validate(new File("resources\\DDMODEL00000545.rdf"));
        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        for(ValidationError validationError:errorList)
            System.out.println(validationError.getQualifier() + validationError.getValue());

    }
}
