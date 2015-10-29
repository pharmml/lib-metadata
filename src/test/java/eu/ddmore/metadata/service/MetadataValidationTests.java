package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.Id;
import eu.ddmore.metadata.api.domain.sections.Section;
import eu.ddmore.metadata.impl.MetadataInformationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:metadatalib-spring-config.xml")
public class MetadataValidationTests {
    @Autowired
    private MetadataValidator metadataValidator;
    @Autowired
    private ValidationReport validationReport;

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
            assertEquals("model-has-name is empty.", errorList.get(0).getMessage());
            assertEquals("model-has-author is empty.", errorList.get(1).getMessage());
            assertEquals("model-has-description is empty.", errorList.get(2).getMessage());
            assertEquals("model-has-description-short is empty.", errorList.get(3).getMessage());
            assertEquals("model-modelling-question is empty.", errorList.get(4).getMessage());
            assertEquals("model-research-stage is empty.", errorList.get(5).getMessage());
            assertEquals("model-field-purpose is empty.", errorList.get(6).getMessage());
            assertEquals("model-tasks-in-scope is empty.", errorList.get(7).getMessage());
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
        ApplicationContext context = new ClassPathXmlApplicationContext("metadatalib-spring-config.xml");
        MetadataInformationService metadataInfo = context.getBean(MetadataInformationService.class);

        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfo.findSectionsForConcept(modelConcept);

        assertEquals(sections.size(),10);

    }
}
