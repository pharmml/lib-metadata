package eu.ddmore.metadata.service;

import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.categories.GenericCategory;
import eu.ddmore.metadata.api.domain.enums.Concept;
import eu.ddmore.metadata.impl.MetadataInformationServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-config.xml")
public class MetadataValidationTests {
    @Autowired
    private MetadataValidator metadataValidator;

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

/*    @Test
    public void testMetadataInformationService(){
        MetadataInformationService mis = new MetadataInformationServiceImpl();
        mis.initialise();
        List<GenericCategory> cats = mis.getMetadataInformationFor(Concept.MODEL);
        System.out.print(cats.size());
    }*/

}
