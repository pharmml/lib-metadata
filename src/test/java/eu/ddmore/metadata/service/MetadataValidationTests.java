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
import com.hp.hpl.jena.rdf.model.Statement;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.Id;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.properties.Property;
import eu.ddmore.metadata.api.domain.sections.Section;
import net.biomodels.jummp.core.model.ValidationState;
import ontologies.OntologySource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

/*    @Test
    public void testEmptyPropertiesValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PharmMLO_0000001");
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
*//*            assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-related-to-disease-or-condition", errorList.get(7).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(7).getErrorStatus());*//*
            assertEquals("http://www.ddmore.org/ontologies/webannotationtool#model-origin-of-code-in-literature-controlled", errorList.get(7).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(7).getErrorStatus());
            assertEquals("http://www.ddmore.org/ontologies/webannotationtool#model-implementation-conforms-to-literature-controlled", errorList.get(8).getQualifier());
            assertEquals(ValidationErrorStatus.EMPTY, errorList.get(8).getErrorStatus());
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void testModelTaskInScopeErrorValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PharmMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-has-name");
        model.add( resource, property, "model 001" );

        property = model.createProperty(metadataNS + "model-has-author");
        model.add( resource, property, "sarala" );

        property = model.createProperty(metadataNS + "model-has-description");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-has-description-short");
        model.add( resource, property, "test model" );

        property = model.createProperty("http://www.ddmore.org/ontologies/webannotationtool#model-implementation-source-discrepancies-freetext");
        model.add(resource, property, "none");

        property = model.createProperty(
"http://www.ddmore.org/ontologies/webannotationtool#model-implementation-conforms-to-literature-controlled"
        );
        model.add(resource, property, "maybe");

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
        modelResource = model.createResource("http://purl.obolibrary.org/obo/DOID_162");
        model.add(resource, property, modelResource);

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        //assertEquals(2, errorList.size());
        Map<String, ValidationErrorStatus> errMap = new TreeMap<String, ValidationErrorStatus>();
        for (ValidationError e: errorList) {
            errMap.put(e.getQualifier(), e.getErrorStatus());
        }
        String tasksInScopeQualifier =
                "http://www.pharmml.org/2013/10/PharmMLMetadata#model-tasks-in-scope";
        assertTrue(errMap.size() > 0);
        assertTrue(errMap.containsKey(tasksInScopeQualifier));
        assertEquals(ValidationErrorStatus.INVALID, errMap.get(tasksInScopeQualifier));

        assertEquals(metadataValidator.getValidationErrorStatus(), ValidationState.CONDITIONALLY_APPROVED);
    }

    @Test
    public void testModelModellingQuestionErrorValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PharmMLO_0000001");
        model.add( resource, property, modelResource );

        property = model.createProperty(metadataNS + "model-has-name");
        model.add( resource, property, "model 001" );

        property = model.createProperty(metadataNS + "model-has-author");
        model.add( resource, property, "sarala" );

        property = model.createProperty(metadataNS + "model-has-description");
        model.add( resource, property, "test model" );

        property = model.createProperty(metadataNS + "model-has-description-short");
        model.add( resource, property, "test model" );

        property = model.createProperty("http://www.ddmore.org/ontologies/webannotationtool#model-implementation-source-discrepancies-freetext");
        model.add(resource, property, "none");

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
        modelResource = model.createResource("http://purl.obolibrary.org/obo/DOID_162");
        model.add(resource, property, modelResource);

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        ArrayList<ValidationError> errorList = metadataValidator.getValidationHandler().getValidationList();
        Map<String, ValidationErrorStatus> errorMap = new TreeMap<String, ValidationErrorStatus>();
        for (ValidationError e: errorList) {
            errorMap.put(e.getQualifier(), e.getErrorStatus());
        }
/*        assertEquals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-modelling-question", errorList.get(0).getQualifier());
        assertEquals(errorList.get(0).getErrorStatus(), ValidationErrorStatus.INVALID);
        assertEquals(errorList.get(0).getValue(), "http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_000603");*/

        String qualifier = metadataNS + "model-tasks-in-scope";
        assertEquals(ValidationErrorStatus.INVALID, errorMap.get(qualifier));

        assertEquals(ValidationState.CONDITIONALLY_APPROVED, metadataValidator.getValidationErrorStatus());
    }

    /* check if a qualifier with value set type TEXTLIST validates correctly. */
    @Test
    public void testTextListValues() {
        final String targetQualifier =
                "http://www.ddmore.org/ontologies/webannotationtool#model-origin-of-code-in-literature-controlled";
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PharmMLO_0000001");
        model.add( resource, property, modelResource );

        // fail if the property is there but the value is bogus
        property = model.createProperty(targetQualifier);
        Statement targetStmt = model.createStatement(resource, property, "NOPE");
        model.add(resource, property, "NOPE");
         try {
            metadataValidator.validate(model);
        } catch (ValidationException expected) { }
        List<ValidationError> errorList =
                metadataValidator.getValidationHandler().getValidationList();
        for (ValidationError e: errorList) {
            if (!e.getQualifier().equals(targetQualifier)) {
                continue;
            }
            assertEquals(ValidationErrorStatus.INVALID, e.getErrorStatus());
            break;
        }

        // stll fail if value is a URI
        model.remove(targetStmt);
        targetStmt = model.createStatement(resource, property, "http://ddg.gg");
        model.add(resource, property, "http://ddg.gg");
         try {
            metadataValidator.validate(model);
        } catch (ValidationException expected) { }
        errorList = metadataValidator.getValidationHandler().getValidationList();
        for (ValidationError e: errorList) {
            if (!e.getQualifier().equals(targetQualifier)) {
                continue;
            }
            assertEquals(ValidationErrorStatus.INVALID, e.getErrorStatus());
            break;
        }

        // now behave nicely
        model.remove(targetStmt);
        model.add(resource, property, "No");

         try {
            metadataValidator.validate(model);
        } catch (ValidationException expected) { }
        errorList = metadataValidator.getValidationHandler().getValidationList();
        for (ValidationError e: errorList) {
            if (e.getQualifier().equals(targetQualifier)) {
                fail("should not encounter error about target qualifier");
            }
        }
    }

    @Test
    public void testValidation(){
        Model model = ModelFactory.createDefaultModel();
        String metadataNS = "http://www.pharmml.org/2013/10/PharmMLMetadata#";
        Resource resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model001");

        com.hp.hpl.jena.rdf.model.Property property;

        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource modelResource = model.createResource("http://www.pharmml.org/ontology/PharmMLO_0000001");
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
        modelResource = model.createResource("http://purl.obolibrary.org/obo/DOID_162");
        model.add(resource, property, modelResource);

        String webannNS = "http://www.ddmore.org/ontologies/webannotationtool#";

        property = model.createProperty(webannNS + "model-origin-of-code-in-literature-controlled");
        model.add(resource, property, "Yes" );

        property = model.createProperty(webannNS + "model-implementation-conforms-to-literature-controlled");
        model.add(resource, property, "No");

        property = model.createProperty(webannNS + "model-implementation-source-discrepancies-freetext");
        model.add(resource, property, "test");

        property = model.createProperty(
                "http://www.ddmore.org/ontologies/webannotationtool#model-has-correspondent");
        model.add(resource, property, "correspondent");

        property = model.createProperty(metadataNS + "model-HasAssumptions-freetext");
        model.add(resource, property, "something");

        // modelling steps annotations
        resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model003");
        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource stepsResource = model.createResource("<http://www.pharmml.org/ontology/PharmMLO_0000007");
        model.add(resource, property, stepsResource);
        model.createProperty(metadataNS + "modellingstep-modelrun-uses-software");
        model.add(resource, property,
                model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0000390"));

        // trial design annotations
        resource = model.createResource("http://www.pharmml.org/database/metadata/MODEL001#model002");
        property = model.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        Resource trialResource = model.createResource("<http://www.pharmml.org/ontology/PharmMLO_0000006");
        model.add(resource, property, trialResource);

        property = model.createProperty(metadataNS + "trialdesign-HasAssociatedDataOfType-DataPool");
        model.add(resource, property, model.createResource(
                "http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0000413"));

        property = model.createProperty(metadataNS + "trialdesign-observed-variables-are-real-freetext");
        model.add(resource, property, "nope");

        model.createProperty(metadataNS + "trialdesign-HasAssociatedHumanGroupByMedicalStatus-DataPool");
        model.add(resource, property,
                model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0008012"));

        model.createProperty(metadataNS + "trialdesign-HasAssociatedNumberOfModelledObservations-DataPool");
        model.add(resource, property, "4");

        model.createProperty(metadataNS + "trialdesign-HasAssociatedNumberOfSubjects-DataPool");
        model.add(resource, property, "4");

        model.createProperty(metadataNS + "trialdesign-HasAssociatedOrganismType-DataPool");
        model.add(resource, property, model.createResource("http://purl.bioontology.org/ontology/NCBITAXON/9986"));

        model.createProperty(metadataNS + "trialdesign-HasAssociatedSamplingDesign-DataPool");
        model.add(resource, property, model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0008035"));

        model.createProperty(metadataNS + "modellingstep-modelrun-uses-software");
        model.add(resource, property, model.createResource("http://www.ddmore.org/ontologies/ontology/pkpd-ontology#pkpd_0000390"));

        model.createProperty(metadataNS + "trialdesign-observed-variables-are-real-freetext");
        model.add(resource, property, "freetext");

/*
http://www.pharmml.org/2013/10/PharmMLMetadata#modellingstep-modelrun-uses-software: EMPTY
http://www.pharmml.org/2013/10/PharmMLMetadata#trialdesign-HasAssociatedDataOfType-DataPool: EMPTY
http://www.pharmml.org/2013/10/PharmMLMetadata#trialdesign-HasAssociatedNumberOfModelledObservations-DataPool: EMPTY
http://www.pharmml.org/2013/10/PharmMLMetadata#trialdesign-HasAssociatedNumberOfSubjects-DataPool: EMPTY
http://www.pharmml.org/2013/10/PharmMLMetadata#trialdesign-HasAssociatedSamplingDesign-DataPool: EMPTY
http://www.pharmml.org/2013/10/PharmMLMetadata#trialdesign-observed-variables-are-real-freetext: EMPTY
*/

        try {
            metadataValidator.validate(model);
        } catch (ValidationException e) {
            e.printStackTrace();
        }

        List<ValidationError> errorList =
                metadataValidator.getValidationHandler().getValidationList();
        Map<String, ValidationErrorStatus> errMap = new TreeMap<String, ValidationErrorStatus>();
        for (ValidationError e: errorList) {
            errMap.put(e.getQualifier(), e.getErrorStatus());
        }
        if (errorList.isEmpty()) {
            System.out.println("hooraaah --- certified");
        } else {
            for (Map.Entry e: errMap.entrySet()) {
                System.out.println(e.getKey() + ": " + e.getValue());
            }
        }

        assertEquals(ValidationState.APPROVED, metadataValidator.getValidationErrorStatus());
    }


    @Test
    public void testMetadataInformationService(){
        List<Section> sections = metadataInfoService.getAllPopulatedRootSections();
        assertEquals(5, sections.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadingAnnotationsFromAnUndefinedFile() {
        File f = null;
        metadataValidator.read(f);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReadingAnnotationsFromAnInexistentFile() {
        metadataValidator.read(new File("somewhere"));
    }

    @Test
    public void testReadingAnnotationsFromAFile() {
        final File f = new File("src/test/resources/MODEL000000000044.rdf");
        assertTrue("The file does not exist or is not readable", f.exists() && f.canRead());
        Model result = metadataValidator.read(f);
        assertEquals(16, result.size());
    }

/*
    @Test
    public void testFieldDiseaseCondition(){
        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PharmMLO_0000001");
        List<Section> sections = metadataInfoService.findSectionsForConcept(modelConcept);
        for(Section section : sections) {
            List<Property> properties = metadataInfoService.findPropertiesForSection(section);
            for(Property property: properties){
                if(property.getPropertyId().getUri().equals("http://www.pharmml.org/2013/10/PharmMLMetadata#model-related-to-disease-or-condition")){
                    if(property.getValueSetType().equals(ValueSetType.ONTOLOGY)){
                        List<OntologySource> sources = metadataInfoService.findOntologyResourcesForProperty(property);
                        assertEquals(sources.get(0).getSourceId(),"doid");
                        assertEquals(metadataValidator.resourceExistInOLS(sources, "test"),false);
                        assertEquals(metadataValidator.resourceExistInOLS(sources, "http://purl.obolibrary.org/obo/DOID_162"),true);
                    }
                }
            }
        }
    }
*/
}
