package eu.ddmore.metadata.service;

import com.ctc.wstx.util.SymbolTable;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.Id;
import eu.ddmore.metadata.api.domain.Property;
import eu.ddmore.metadata.api.domain.Value;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.Section;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:metadatalib-spring-config.xml")
public class MetadataValidationTests {
    @Autowired
    private MetadataValidator metadataValidator;


    @Test
    public void testDdmoreCertified() {
/*        URL url = null;
        try {
            url = new URL("http://wwwdev.ebi.ac.uk/biomodels/model-repository/model/download/DDMODEL00000413.1?filename=Friberg_2009_Schizophrenia_Asenapine_PANSS_20140924_v5_Nonmem-validated.rdf");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }*/

        File file = new File("resources\\example2.xml");
     //   assertFalse(metadataValidator.validate(url.toString()));
    }

    @Test(expected = ValidationException.class)
    public void testUntypedModelValidationException() throws ValidationException {
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";

        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");
        com.hp.hpl.jena.rdf.model.Property property = model.createProperty(metadataNS + "has-submissionId");
        model.add( resource, property, "MODEL001" );
        metadataValidator.setModel(model);
        metadataValidator.validate("MODEL001");
    }

    @Test(expected = ValidationException.class)
    public void testMissingSubmissionIdValidationException() throws ValidationException {
        Model model = ModelFactory.createDefaultModel();
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");
        com.hp.hpl.jena.rdf.model.Property property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );
        metadataValidator.setModel(model);
        metadataValidator.validate("MODEL001");
    }

    @Test(expected = ValidationException.class)
    public void testInValidSubmissionIDValidationException() throws ValidationException {
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");
        com.hp.hpl.jena.rdf.model.Property property;

        property= model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "has-submissionId");
        model.add( resource, property, "MODEL001" );

        metadataValidator.setModel(model);
        metadataValidator.validate("MODEL002");
    }

    @Test
    public void testEmptyPropertiesValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "has-submissionId");
        model.add(resource, property, "MODEL001");

        metadataValidator.setModel(model);
        try {
            metadataValidator.validate("MODEL001");
            ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
            assertEquals(errorList.get(0).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-name is empty.");
            assertEquals(errorList.get(1).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-author is empty.");
            assertEquals(errorList.get(2).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-description is empty.");
            assertEquals(errorList.get(3).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-description-short is empty.");
            assertEquals(errorList.get(4).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-modelling-question is empty.");
            assertEquals(errorList.get(5).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-research-stage is empty.");
            assertEquals(errorList.get(6).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-field-purpose is empty.");
            assertEquals(errorList.get(7).getMessage(),"http://www.pharmml.org/2013/10/PharmMLMetadata#model-tasks-in-scope is empty.");
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testModelTaskInScopeErrorValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "has-submissionId");
        model.add( resource, property, "MODEL001" );

        property = model.createProperty(metadataNS + "model-has-name");
        model.add( resource, property, "model 001" );

        property = model.createProperty(metadataNS + "model-has-author");
        model.add( resource, property, "sarala" );

        property = model.createProperty(metadataNS + "model-has-description");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-has-description-short");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-modelling-question");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006036");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-research-stage");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006010");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-field-purpose");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0001023");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-tasks-in-scope");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_test");
        model.add( resource, property, modelResource );

        metadataValidator.setModel(model);

        try {
            metadataValidator.validate("MODEL001");
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        assertEquals(errorList.get(0).getErrorStatus(),ValidationErrorStatus.INFO);
        assertEquals(errorList.get(1).getErrorStatus(), ValidationErrorStatus.INFO);
        assertEquals(errorList.get(2).getErrorStatus(),ValidationErrorStatus.INFO);
        assertEquals(errorList.get(3).getErrorStatus(),ValidationErrorStatus.INFO);
        assertEquals(errorList.get(4).getErrorStatus(), ValidationErrorStatus.INFO);
        assertEquals(errorList.get(5).getErrorStatus(),ValidationErrorStatus.INFO);
        assertEquals(errorList.get(6).getErrorStatus(),ValidationErrorStatus.INFO);
        assertEquals(errorList.get(7).getErrorStatus(), ValidationErrorStatus.ERROR);

        assertEquals(metadataValidator.getValidationErrorStatus(), ValidationStatus.CONDITIONALLY_APPROVED);
    }

    @Test
    public void testValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "has-submissionId");
        model.add( resource, property, "MODEL001" );

        property = model.createProperty(metadataNS + "model-has-name");
        model.add( resource, property, "model 001" );

        property = model.createProperty(metadataNS + "model-has-author");
        model.add( resource, property, "sarala" );

        property = model.createProperty(metadataNS + "model-has-description");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-has-description-short");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-modelling-question");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006036");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-research-stage");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006010");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-field-purpose");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0001023");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-tasks-in-scope");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006000");
        model.add( resource, property, modelResource );

        metadataValidator.setModel(model);

        try {
            metadataValidator.validate("MODEL001");
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        assertEquals(metadataValidator.getValidationErrorStatus(), ValidationStatus.APPROVED);
    }


    @Test
    public void testMetadataInformationService(){
        MetadataInformationService metadataInfo = new MetadataInformationServiceImpl();
        metadataInfo.initialise();

        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfo.findSectionsForConcept(modelConcept);

        assertEquals(sections.size(),10);

    }

}
