package eu.ddmore.metadata.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class MetadataValidationTests {
    @Autowired
    private MetadataValidator metadataValidator;
    @Autowired
    private ValidationReport validationReport;

    @Test
    public void testDdmoreCertified() {
        URL url = null;
        try {
            url = new URL("http://wwwdev.ebi.ac.uk/biomodels/model-repository/model/download/DDMODEL00000413.1?filename=Friberg_2009_Schizophrenia_Asenapine_PANSS_20140924_v5_Nonmem-validated.rdf");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assertFalse(metadataValidator.ddmoreCertified(url.toString()));
    }

    @Test
    public void metadataStatementListIsEmpty() {
        File f = new File("resources", "example1.xml");
        assertTrue(f.exists());
        // TODO Why does the method always return false?
        assertFalse(metadataValidator.ddmoreCertified(f.toURI().toString()));
        String exp = "Validation Report: example1.xml\nThis is a valid DDMoRe certified metadata document";
        assertEquals(exp, validationReport.generateValidationReport(f));
        // Should metadataStatements be empty after parsing the annotations?
        assertEquals(0, metadataValidator.getMetadataStatements().size());
    }

    @Test
    public void emptyAnnotationFilesAreOK() {
        File f = new File("resources", "noAnnotations.xml");
        assertTrue(f.exists());
        assertFalse(metadataValidator.ddmoreCertified(f.toURI().toString()));
        String exp = "Validation Report: noAnnotations.xml\nThis is a valid DDMoRe certified metadata document";
        assertEquals(exp, validationReport.generateValidationReport(f));
    }
}
