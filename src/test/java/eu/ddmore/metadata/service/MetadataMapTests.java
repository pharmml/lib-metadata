package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 17/03/2015
 *         Time: 11:06
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class MetadataMapTests {

    @Autowired
    private MetadataMap metadataMap;

    @Test
    public void testPropertiesMap() {
        Resource resource = ResourceFactory.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        ArrayList<Property> properties = metadataMap.getAssociatedProperties(resource);
        assertEquals(9, properties.size());
    }

    @Test
    public void testOntologyMap() {
        Property property = ResourceFactory.createProperty("http://www.pharmml.org/2013/10/PharmMLMetadata#model-field-purpose");
        ArrayList<Resource> resources = metadataMap.getAssociatedResources(property);
        assertEquals(14, resources.size());
    }
}
