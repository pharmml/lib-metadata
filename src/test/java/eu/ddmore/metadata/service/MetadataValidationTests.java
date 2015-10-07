package eu.ddmore.metadata.service;

import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.Id;
import eu.ddmore.metadata.api.domain.Property;
import eu.ddmore.metadata.api.domain.Value;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.Section;
import eu.ddmore.metadata.impl.MetadataInformationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:metadatalib-spring-config.xml")
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

    @Test
    public void testMetadataInformationService(){
        MetadataInformationService metadataInfo = new MetadataInformationServiceImpl();
        metadataInfo.initialise();

        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfo.findSectionsForConcept(modelConcept);

        for(Section section: sections) {
            List<Property> properties = metadataInfo.findPropertiesForSection(section);
            for(Property property: properties){
                System.out.println(property.isRequired() +" " +section.getSectionNumber()+ " " +property.getPropertyId().getUri());

                if(property.getValueSetType() != ValueSetType.TEXT) {

                    List<Value> values = metadataInfo.findValuesForProperty(property);
                    for (Value value : values) {
                        System.out.println("\t" + value.getValueId().getUri());

                    }
                }
            }
        }

    }

}
