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
            MetadataReader metadataReader = context.getBean(MetadataReaderImpl.class);
            MetadataMap metadataMap = metadataReader.metadataRead(new File("C:\\Users\\sarala.EBI\\Documents\\GitHub\\pharmml-metadata\\specification\\DDMODEL00000186-metadata.rdf"));
            MetadataValidator metadataValidator = context.getBean(MetadataValidatorImpl.class);
            metadataValidator.setMetadataMap(metadataMap);
            System.out.println(metadataValidator.validationReport());

        }
}
