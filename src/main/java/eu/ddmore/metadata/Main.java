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
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        MetadataValidator metadataValidator = context.getBean(MetadataValidatorImpl.class);
        metadataValidator.ddmoreCertified(new File("resources\\example2.xml"));
        System.out.println(metadataValidator.validationReport());

        }
}
