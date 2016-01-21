/*
 * Copyright 2015 EMBL-EBI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.Id;
import eu.ddmore.metadata.api.domain.sections.Section;
import net.biomodels.jummp.core.model.ValidationState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    private MetadataInformationService metadataInfoService;

    @Test(expected = ValidationException.class)
    public void testUntypedModelValidationException() throws ValidationException {
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";

        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");
        metadataValidator.validate(model);
    }

    @Test
    public void testEmptyPropertiesValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add(resource, property, modelResource);

        try {
            metadataValidator.validate(model);
            ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-name", errorList.get(0).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(0).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-author", errorList.get(1).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(1).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-description", errorList.get(2).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(2).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-has-description-short", errorList.get(3).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(3).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-modelling-question", errorList.get(4).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(4).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-research-stage", errorList.get(5).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(5).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-field-purpose", errorList.get(6).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(6).getErrorStatus());
            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-related-to-disease-or-condition", errorList.get(7).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(7).getErrorStatus());
            assertEquals("http://www.ddmore.org/ontologies/webannotationtool#model-origin-of-code-in-literature-controlled", errorList.get(8).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(8).getErrorStatus());
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
        model.add(resource, property, modelResource);

        property = model.createProperty(metadataNS + "model-related-to-disease-or-condition");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_test");
        model.add(resource, property, modelResource);

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        assertEquals("http://www.ddmore.org/ontologies/webannotationtool#model-origin-of-code-in-literature-controlled", errorList.get(0).getQualifier());
        assertEquals(errorList.get(0).getErrorStatus(), ValidationErrorStatus.EMPTY);

        assertEquals(metadataValidator.getValidationErrorStatus(), ValidationState.CONDITIONALLY_APPROVED);
    }

    @Test
    public void testModelModellingQuestionErrorValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-has-name");
        model.add( resource, property, "model 001" );

        property = model.createProperty(metadataNS + "model-has-author");
        model.add( resource, property, "sarala" );

        property = model.createProperty(metadataNS + "model-has-description");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-has-description-short");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-modelling-question");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_000603");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-research-stage");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0006010");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-field-purpose");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0001023");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-tasks-in-scope");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_test");
        model.add(resource, property, modelResource);

        property = model.createProperty(metadataNS + "model-related-to-disease-or-condition");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_test");
        model.add(resource, property, modelResource);

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-modelling-question", errorList.get(0).getQualifier());
        assertEquals(errorList.get(0).getErrorStatus(), ValidationErrorStatus.INVALID);
        assertEquals(errorList.get(0).getValue(), "http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_000603");

        assertEquals("http://www.ddmore.org/ontologies/webannotationtool#model-origin-of-code-in-literature-controlled", errorList.get(1).getQualifier());
        assertEquals(errorList.get(1).getErrorStatus(), ValidationErrorStatus.EMPTY);

        assertEquals(metadataValidator.getValidationErrorStatus(), ValidationState.CONDITIONALLY_APPROVED);
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
        model.add(resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-related-to-disease-or-condition");
        modelResource = model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_test");
        model.add(resource, property, modelResource);

        String webannNS = "http://www.ddmore.org/ontologies/webannotationtool#";

        property = model.createProperty(webannNS + "model-origin-of-code-in-literature-controlled");
        modelResource = model.createResource("true");
        model.add(resource, property, modelResource );

        property = model.createProperty(webannNS + "model-implementation-conforms-to-literature-controlled");
        modelResource = model.createResource("true");
        model.add(resource, property, modelResource);

        property = model.createProperty(webannNS + "model-implementation-source-discrepancies-freetext");
        model.add( resource, property, "test" );

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        assertEquals(ValidationState.APPROVED, metadataValidator.getValidationErrorStatus());
    }


    @Test
    public void testMetadataInformationService(){
        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfoService.findSectionsForConcept(modelConcept);

        assertEquals(sections.size(),15);

    }
}
