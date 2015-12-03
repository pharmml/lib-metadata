package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Property;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.*;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.Section;
import eu.ddmore.metadata.api.domain.values.CompositeValue;
import eu.ddmore.metadata.api.domain.values.Value;
import net.biomodels.jummp.core.model.ValidationState;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;


import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    public void validate(File file)throws ValidationException {
        model = ModelFactory.createDefaultModel();
        model.read(file.toURI().toString());
        validate();
    }

    public void validate(Model model)throws ValidationException{
        this.model = model;
        validate();
    }

    private void validate() throws ValidationException {
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

        if (!requiredProperties.isEmpty())
            validate(requiredProperties, resource);


    }

    private void validate(List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties, Resource resource)  {
         for (eu.ddmore.metadata.api.domain.properties.Property requiredProperty : requiredProperties) {
                Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                StmtIterator stmtIterator = resource.listProperties(property);
                if (stmtIterator!=null){
                    if(!stmtIterator.hasNext()){
                        validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, requiredProperty.getPropertyId().getUri(), validationMessage(resource, null, 0)));
                    }
                    while (stmtIterator.hasNext()){
                        Statement statement = stmtIterator.nextStatement();

                        int validationLevel = validationLevel(requiredProperty,statement.getObject());
                        if(validationLevel != -1){
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.ERROR, requiredProperty.getPropertyId().getUri(), validationMessage(resource, statement.getObject(), validationLevel)));
                        }
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
        List<Value> associatedResources = null;
        if(property.getValueSetType().equals(ValueSetType.TEXT)){
            return -1;
        }
        else if(property.getValueSetType().equals(ValueSetType.LIST)){
            associatedResources = metadataInfoService.findValuesForProperty(property);
        }
        else if(property.getValueSetType().equals(ValueSetType.TREE)){
            associatedResources = getassociatedResourcesFromTree(metadataInfoService.findValuesForProperty(property));
        }

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

    private List<Value> getassociatedResourcesFromTree(List<Value> associatedResources){
        List<Value> resouces = new ArrayList<Value>();
        for(Value validResource: associatedResources){
            if(validResource.isValueTree()) {
                CompositeValue compositeValue = (CompositeValue) validResource;
                resouces.addAll(compositeValue.getValues());
                getassociatedResourcesFromTree(compositeValue.getValues());
            }
            else return associatedResources;
        }
        associatedResources.addAll(resouces);
        return associatedResources;

    }

    private String validationMessage(Resource subject, RDFNode rdfNode, int validationLevel){
        String validationStatement = "";
        String rdfNodeValue = "";
        if(rdfNode!=null) {
            if (rdfNode.isLiteral()) {
                rdfNodeValue = ((Literal) rdfNode).getString();
                if(rdfNodeValue.length()>50){
                    rdfNodeValue = rdfNodeValue.substring(0, 50)+ "...";
                }
            }
            else if (rdfNode.isResource()) {
                rdfNodeValue = ((Resource) rdfNode).getLocalName();
                rdfNodeValue = getOntologyLabel(rdfNodeValue);
            }
        }

        switch (validationLevel) {
            case -1:
                validationStatement = " is "+ rdfNodeValue + ".";
                break;
            case 0:
                validationStatement = " is empty.";
                break;
            case 1:
                validationStatement = " "+ rdfNodeValue + " is invalid.";
                break;
        }
        return validationStatement;
    }

    private String getOntologyLabel(String rdfNodeValue){
        String urlString = "http://open-physiology.org:20080/labels/"+rdfNodeValue+"?json=true";
        try {
            URL url = new URL(urlString);
            JSONTokener tokener = new JSONTokener(IOUtils.toString(url.openStream()));
            JSONArray array = new JSONArray(tokener);
            // The first label is picked
            if (array != null && array.length() >= 1){
                rdfNodeValue = array.get(0).toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            logger.error(e.getMessage());
        }
        return rdfNodeValue;

    }

    public ValidationHandler getValidationHandler() {
        return validationHandler;
    }

    public ValidationState getValidationErrorStatus(){
        if(validationHandler.getValidationList().isEmpty())
            return ValidationState.APPROVED;
        else
            return ValidationState.CONDITIONALLY_APPROVED;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}
