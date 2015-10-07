package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.*;

import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 28/09/2015
 *         Time: 10:30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:metadatalib-spring-config.xml")
public class MetadataWriterTests {
    @Autowired
    private MetadataWriter metadataWriter;

    @Before
    public void setUp() throws Exception {
        metadataWriter.generateLiteralTriple("http://www.pharmml.org/MODEL0001", "http://www.pharmml.org/metadata#name", "Test model");
        metadataWriter.generateTriple("http://www.pharmml.org/MODEL0001", "http://www.pharmml.org/metadata#pubid", "http://identifiers.org/pubid");
        metadataWriter.generateLiteralTriple("http://www.pharmml.org/MODEL0001", "http://www.pharmml.org/metadata#created", Calendar.getInstance().getTime());
    }

    @Test
    public void testGenerateTriple() {
        Model model = metadataWriter.getModel();
        assertEquals("http://www.pharmml.org/MODEL0001", model.listSubjects().nextResource().getURI());
        assertEquals("Test model", ((Literal) model.listObjectsOfProperty(model.createProperty("http://www.pharmml.org/metadata#name")).nextNode()).getString());
        assertEquals("http://identifiers.org/pubid", ((Resource) model.listObjectsOfProperty(model.createProperty("http://www.pharmml.org/metadata#pubid")).nextNode()).getURI());
    }

}
