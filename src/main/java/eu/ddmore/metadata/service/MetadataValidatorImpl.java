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

    public void readModel(String url){
        model = ModelFactory.createDefaultModel();
        model.read(url);
    }

    public void validate(String submissionId) throws ValidationException {
        validationHandler = new ValidationHandler();
        Resource resource = validateBasics(submissionId);
        validateModelConcept(resource);
    }

    private Resource validateBasics(String submissionId) throws ValidationException {
        Resource resourceEntry = ResourceFactory.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        ResIterator resIterator = model.listSubjectsWithProperty(typeProperty, resourceEntry);

        if(!resIterator.hasNext()){
            throw new ValidationException("Invalid RDF document. The metadata is not be typed");
        }

        Resource resource = resIterator.nextResource();

        NodeIterator nodeIterator = model.listObjectsOfProperty(resource, ResourceFactory.createProperty("http://www.pharmml.org/2013/10/PharmMLMetadata#has-submissionId"));
        if (!nodeIterator.hasNext()){
            throw new ValidationException("Invalid RDF document. The metadata is not associated with a submission");
        }
        else{
            RDFNode rdfNode = nodeIterator.nextNode();
            if(rdfNode.isLiteral()) {
                if(!((Literal)rdfNode).getString().equals(submissionId)){
                    throw new ValidationException("Invalid RDF document. The metadata is not associated with the correct submission");
                }
            }
        }
        return resource;

    }


    private void validateModelConcept(Resource resource) throws ValidationException {
        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfoService.findSectionsForConcept(modelConcept);
        List<eu.ddmore.metadata.api.domain.Property> requiredProperties = getRequiredProperties(sections);

        logger.info("Number of required properties for the concept" + modelConcept.getLabel() + " is " + requiredProperties.size());


        if (requiredProperties.isEmpty())
            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO, "There are no required properties to validate."));
        else
            validate(requiredProperties, resource);


    }

    private void validate(List<eu.ddmore.metadata.api.domain.Property> requiredProperties, Resource resource)  {
         for (eu.ddmore.metadata.api.domain.Property requiredProperty : requiredProperties) {
                Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                StmtIterator stmtIterator = resource.listProperties(property);
                if (stmtIterator!=null){
                    if(!stmtIterator.hasNext()){
                        validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resource, property, null, 0)));
                    }
                    while (stmtIterator.hasNext()){
                        Statement statement = stmtIterator.nextStatement();

                        int validationLevel = validationLevel(requiredProperty,statement.getObject());
                        if(validationLevel != -1){
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resource, property, statement.getObject(), validationLevel)));
                        }
                        else
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO,validationMessage(resource,property,statement.getObject(),validationLevel)));

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
                rdfNodeValue = ((Resource) rdfNode).getLocalName() ;
        }

        switch (validationLevel) {
            case -1:
                validationStatement = subject.getLocalName() + " : " + property.getLocalName() + " : "+ rdfNodeValue + ".";
                break;
            case 0:
                validationStatement = property.getLocalName() + " is empty.";
                break;
            case 1:
                validationStatement = subject.getLocalName()  + " : " + property.getLocalName()  + " : "+ rdfNodeValue + " is invalid.";
                break;
        }
        return validationStatement;
    }

    public ValidationHandler getValidationHandler() {
        return validationHandler;
    }

    public ValidationStatus getValidationErrorStatus(){
        if(validationHandler.getValidationList().isEmpty())
            return ValidationStatus.APPROVE;
        for(ValidationError validationError: validationHandler.getValidationList()){
            if(validationError.getErrorStatus()==ValidationErrorStatus.ERROR){
                return ValidationStatus.CONDITIONALLY_APPROVED;
            }
        }

        return ValidationStatus.APPROVED;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
