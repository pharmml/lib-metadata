package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Property;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.*;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.Section;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 14:38
 */
public class MetadataValidatorImpl implements MetadataValidator{

    private static final Log logger = LogFactory.getLog(MetadataValidatorImpl.class);
    private MetadataInformationService metadataInfoService;
    private Model model;
    private ValidationHandler validationHandler;

    public MetadataValidatorImpl(MetadataInformationService metadataInfoService) {
        this.metadataInfoService = metadataInfoService;
    }

    public boolean ddmoreCertified(String url) {
        validationHandler = new ValidationHandler();
        model = ModelFactory.createDefaultModel();
        model.read(url);

        validateModelConcept();

        return false;
    }

    private void validateModelConcept(){
        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfoService.findSectionsForConcept(modelConcept);
        List<eu.ddmore.metadata.api.domain.Property> requiredProperties = getRequiredProperties(sections);
        logger.info("Number of required properties for the concept" + modelConcept.getLabel() + " is " + requiredProperties.size());

        if (requiredProperties.isEmpty())
            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO, "There are no required properties to validate."));
        else
            validate(modelConcept, requiredProperties);


    }

    private void validate(Id modelConcept, List<eu.ddmore.metadata.api.domain.Property> requiredProperties)  {

        Resource resourceEntry = ResourceFactory.createResource(modelConcept.getUri());
        Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        ResIterator resIterator = model.listSubjectsWithProperty(typeProperty, resourceEntry);

        if(!requiredProperties.isEmpty() && !resIterator.hasNext()){
            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, "Required properties are missing. Model may not be typed."));
        }

            while (resIterator.hasNext()) {
                Resource resource = resIterator.nextResource();
                for (eu.ddmore.metadata.api.domain.Property requiredProperty : requiredProperties) {
                    Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                    StmtIterator stmtIterator = resource.listProperties(property);
                    if (stmtIterator!=null){
                        if(!stmtIterator.hasNext()){
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resourceEntry, property, null, 0)));
                        }
                        while (stmtIterator.hasNext()){
                            Statement statement = stmtIterator.nextStatement();

                            int validationLevel = validationLevel(requiredProperty,statement.getObject());
                            if(validationLevel != -1){
                                validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resourceEntry, property, statement.getObject(), validationLevel)));
                            }
                            else
                                validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO,validationMessage(resourceEntry,property,statement.getObject(),validationLevel)));

                        }
                    }
                }
            }

    }


    private List<eu.ddmore.metadata.api.domain.Property> getRequiredProperties(List<Section> sections){
        List<eu.ddmore.metadata.api.domain.Property> requiredProperties = new ArrayList<eu.ddmore.metadata.api.domain.Property>();

        for(Section section: sections) {
            for(eu.ddmore.metadata.api.domain.Property property: metadataInfoService.findPropertiesForSection(section)){
                if(property.isRequired()){
                    requiredProperties.add(property);
                }
            }
        }
        return requiredProperties;

    }

    public int validationLevel(eu.ddmore.metadata.api.domain.Property property, RDFNode rdfNode){
        if(property.getValueSetType().equals(ValueSetType.TEXT)){
            return -1;
        }
        List<Value> associatedResources = metadataInfoService.findValuesForProperty(property);
        if(associatedResources==null){
            return -1;
        }
        else{
            if(rdfNode.isLiteral()){
                return 1;
            }
            else if(rdfNode.isResource()){
                Resource givenOntoResource = (Resource)rdfNode;
                for(Value validResource: associatedResources){
                    if(validResource.getValueId().getUri().equals(givenOntoResource.getURI())){
                        return -1;
                    }
                }
                return 1;
            }
            else
                return 1;
        }
    }

    private String validationMessage(Resource subject, Property property, RDFNode rdfNode, int validationLevel){
        String validationStatement = "";
        String rdfNodeValue = "";
        if(rdfNode!=null) {
            if (rdfNode.isLiteral()) {
                rdfNodeValue = ((Literal) rdfNode).getString();
                if(rdfNodeValue.length()>50){
                    rdfNodeValue = rdfNodeValue.substring(0, 50)+ "...";
                }
            }
            else if (rdfNode.isResource())
                rdfNodeValue = ((Resource) rdfNode).getURI();
        }

        switch (validationLevel) {
            case -1:
                validationStatement = subject + " : " + property + " : "+ rdfNodeValue + "";
                break;
            case 0:
                validationStatement = property + " is empty.";
                break;
            case 1:
                validationStatement = subject + " : " + property + " : "+ rdfNodeValue + " ==> Annotation is not valid.";
                break;
        }
        return validationStatement;
    }

    public ValidationHandler getValidationHandler() {
        return validationHandler;
    }
}
