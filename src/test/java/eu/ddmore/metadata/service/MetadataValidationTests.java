package eu.ddmore.metadata.service;

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

    @Test
    public void testDdmoreCertified() {
        org.junit.Assert.assertFalse(metadataValidator.ddmoreCertified(new File("resources\\example2.xml")));
    }
}
