package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Property;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.*;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.Section;
import eu.ddmore.metadata.api.domain.values.Value;
import net.biomodels.jummp.core.model.ValidationState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 14:38
 */
public class MetadataValidatorImpl implements MetadataValidator{

    /**
     * The class logger.
     */
    private static final Log logger = LogFactory.getLog(MetadataValidatorImpl.class);
    /**
     * Flag indicating the verbosity of the class logger.
     */
    private static final boolean IS_DEBUG_ENABLED = logger.isDebugEnabled();

    private MetadataInformationService metadataInfoService;
    private Model model;
    private ValidationHandler validationHandler;

    public MetadataValidatorImpl(MetadataInformationService metadataInfoService) {
        this.metadataInfoService = metadataInfoService;
    }

    /**
     * Reads annotations from the supplied file
     *
     */
    public Model read(File file) throws IllegalArgumentException {
        if (null == file || !(file.canRead())) {
            throw new IllegalArgumentException("Please pass a file that I have read access to.");
        }
        if (IS_DEBUG_ENABLED) {
            logger.debug("Started reading annotations from " + file.getName());
        }
        readModel(file.toURI().toString());
        if (IS_DEBUG_ENABLED) {
            logger.debug("Finished reading annotations from " + file.getName());
        }
        return this.model;
    }

    public void readModel(String url){
        model = ModelFactory.createDefaultModel();
        model.read(url);
    }

    public void validate() throws ValidationException {
        validationHandler = new ValidationHandler();
        Resource resource = validateBasics();
        validateModelConcept(resource);
    }

    private Resource validateBasics() throws ValidationException {
        Resource resourceEntry = ResourceFactory.createResource("http://www.pharmml.org/ontology/PHARMMLO_0000001");
        Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        ResIterator resIterator = model.listSubjectsWithProperty(typeProperty, resourceEntry);

        if(!resIterator.hasNext()){
            throw new ValidationException("Invalid RDF document. The metadata is not be typed");
        }

        return resIterator.nextResource();

    }


    private void validateModelConcept(Resource resource) throws ValidationException {
        Id modelConcept = new Id("Model","http://www.pharmml.org/ontology/PHARMMLO_0000001");
        List<Section> sections = metadataInfoService.findSectionsForConcept(modelConcept);
        List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties = getRequiredProperties(sections);

        logger.info("Number of required properties for the concept" + modelConcept.getLabel() + " is " + requiredProperties.size());


        if (requiredProperties.isEmpty())
            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO, "There are no required properties to validate."));
        else
            validate(requiredProperties, resource);


    }

    private void validate(List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties, Resource resource)  {
         for (eu.ddmore.metadata.api.domain.properties.Property requiredProperty : requiredProperties) {
                Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                StmtIterator stmtIterator = resource.listProperties(property);
                if (stmtIterator!=null){
                    if(!stmtIterator.hasNext()){
                        validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resource, requiredProperty, null, 0)));
                    }
                    while (stmtIterator.hasNext()){
                        Statement statement = stmtIterator.nextStatement();

                        int validationLevel = validationLevel(requiredProperty,statement.getObject());
                        if(validationLevel != -1){
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, validationMessage(resource, requiredProperty, statement.getObject(), validationLevel)));
                        }
                        else
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INFO,validationMessage(resource,requiredProperty,statement.getObject(),validationLevel)));

                    }
                }
            }


    }


    private List<eu.ddmore.metadata.api.domain.properties.Property> getRequiredProperties(List<Section> sections){
        List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties = new ArrayList<eu.ddmore.metadata.api.domain.properties.Property>();

        for(Section section: sections) {
            for(eu.ddmore.metadata.api.domain.properties.Property property: metadataInfoService.findPropertiesForSection(section)){
                if(property.isRequired()){
                    requiredProperties.add(property);
                }
            }
        }
        return requiredProperties;

    }

    public int validationLevel(eu.ddmore.metadata.api.domain.properties.Property property, RDFNode rdfNode){
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

    private String validationMessage(Resource subject, eu.ddmore.metadata.api.domain.properties.Property property, RDFNode rdfNode, int validationLevel){
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
                validationStatement = subject.getLocalName() + " " + property.getPropertyId().getLabel() + " is "+ rdfNodeValue + ".";
                break;
            case 0:
                validationStatement = property.getPropertyId().getLabel() + " is empty.";
                break;
            case 1:
                validationStatement = subject.getLocalName()  + " " + property.getPropertyId().getLabel()  + " "+ rdfNodeValue + " is invalid.";
                break;
        }
        return validationStatement;
    }

    public ValidationHandler getValidationHandler() {
        return validationHandler;
    }

    public ValidationState getValidationErrorStatus(){
        if(validationHandler.getValidationList().isEmpty())
            return ValidationState.APPROVE;
        for(ValidationError validationError: validationHandler.getValidationList()){
            if(validationError.getErrorStatus()==ValidationErrorStatus.ERROR){
                return ValidationState.CONDITIONALLY_APPROVED;
            }
        }

        return ValidationState.APPROVED;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
