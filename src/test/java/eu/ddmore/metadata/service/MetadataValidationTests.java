package eu.ddmore.metadata.service;

import eu.ddmore.metadata.service.MetadataValidator;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class MetadataValidationTests {
    @Autowired
    private MetadataValidator metadataValidator;

    @Autowired
    private MetadataReader metadataReader;

    @Autowired
    private MetadataMap metadataMap;

    @Before
    public void setUp() throws Exception {
        metadataMap = metadataReader.metadataRead(new File("C:\\Users\\sarala.EBI\\Documents\\GitHub\\pharmml-metadata\\specification\\DDMODEL00000186-metadata.rdf"));
        metadataValidator.setMetadataMap(metadataMap);

    }

    @Test
    public void testDdmoreCertified() {
        org.junit.Assert.assertTrue(metadataValidator.ddmoreCertified());
    }
}
