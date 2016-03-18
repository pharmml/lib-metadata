package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.Property;
import eu.ddmore.metadata.api.MetadataInformationService;
import eu.ddmore.metadata.api.domain.*;
import eu.ddmore.metadata.api.domain.enums.ValueSetType;
import eu.ddmore.metadata.api.domain.sections.CompositeSection;
import eu.ddmore.metadata.api.domain.sections.Section;
import eu.ddmore.metadata.api.domain.values.CompositeValue;
import eu.ddmore.metadata.api.domain.values.Value;
import net.biomodels.jummp.core.model.ValidationState;
import ontologies.OntologySource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
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
        model = ModelFactory.createDefaultModel();
        model.read(file.toURI().toString());
        if (IS_DEBUG_ENABLED) {
            logger.debug("Finished reading annotations from " + file.getName());
        }
        return model;
    }

    public void validate(File file)throws ValidationException {
        read(file);
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
        List<Section> sections = new ArrayList<Section>();
        getAllSections(metadataInfoService.getAllPopulatedRootSections(), sections);
        List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties = getRequiredProperties(sections);

        logger.info("Number of required properties for the concept" + modelConcept.getLabel() + " is " + requiredProperties.size());

        if (!requiredProperties.isEmpty())
            validate(requiredProperties, resource);


    }

    private void getAllSections(List<Section> sections, List<Section> sectionsToValidate){
        for (Section section : sections) {
            if(section instanceof CompositeSection){
                getAllSections(((CompositeSection)section).getSections(), sectionsToValidate);
            }
            else {
                sectionsToValidate.add(section);
            }
        }


    }

    private void validate(List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties, Resource resource)  {
         for (eu.ddmore.metadata.api.domain.properties.Property requiredProperty : requiredProperties) {
                Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                StmtIterator stmtIterator = resource.listProperties(property);
                if (stmtIterator!=null){
                    if(!stmtIterator.hasNext()){
                        validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.EMPTY, requiredProperty.getPropertyId().getUri()));
                    }
                    while (stmtIterator.hasNext()){
                        Statement statement = stmtIterator.nextStatement();

                        boolean isValid = isValid(requiredProperty, statement.getObject());
                        if(!isValid){
                            validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INVALID, requiredProperty.getPropertyId().getUri(), getRDFNodeValue(statement.getObject())));
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

    public boolean isValid(eu.ddmore.metadata.api.domain.properties.Property property, RDFNode rdfNode){
        List<Value> associatedResources = null;
        if(property.getValueSetType().equals(ValueSetType.TEXT)){
            return true;
        }
        else if(property.getValueSetType().equals(ValueSetType.LIST)){
            associatedResources = metadataInfoService.findValuesForProperty(property);
        }
        else if(property.getValueSetType().equals(ValueSetType.TREE)){
            associatedResources = getassociatedResourcesFromTree(metadataInfoService.findValuesForProperty(property));
        }
        else if(property.getValueSetType().equals(ValueSetType.ONTOLOGY)){
            if(rdfNode.isResource()) {
                List<OntologySource> sources = metadataInfoService.findOntologyResourcesForProperty(property);
                return resourceExistInOLS(sources, ((Resource) rdfNode).getURI());
            }else{
                //if property is an ontology, rdfNode must be a resource
                return false;
            }
        }

        if(associatedResources==null){
            return true;
        }
        else{
            if(rdfNode.isLiteral()){
                return false;
            }
            else if(rdfNode.isResource()){
                Resource givenOntoResource = (Resource)rdfNode;
                for(Value validResource: associatedResources){
                    if(validResource.getValueId().getUri().equals(givenOntoResource.getURI())){
                        return true;
                    }
                }
                return false;
            }
            else
                return false;
        }
    }

    public boolean resourceExistInOLS(List<OntologySource> sources, String uri){
        for(OntologySource ontologySource: sources){
            if(ontologySource.getResource().equals("OLS")){
                String iri = uri;
                try {
                    iri = URLEncoder.encode(iri, "UTF-8");
                    iri = URLEncoder.encode(iri, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String urlString = "http://www.ebi.ac.uk/ols/beta/api/ontologies/"+ontologySource.getSourceId()+"/terms/"+iri;

                try {
                    URL url = new URL(urlString);
                    JSONTokener tokener = new JSONTokener(IOUtils.toString(url.openStream()));
                    JSONObject jsonObject = new JSONObject(tokener);
                    String value = (String)jsonObject.get("iri");
                    if(value.equals(uri)){
                        return true;
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage());
                } catch (JSONException e) {
                    logger.error(e.getMessage());
                }
            }
        }
        return false;

    }



    private List<Value> getassociatedResourcesFromTree(List<Value> associatedResources){
        List<Value> resouces = new ArrayList<Value>();
        for(Value validResource: associatedResources){
            if (validResource.isValueTree()) {
                CompositeValue compositeValue = (CompositeValue) validResource;
                resouces.addAll(compositeValue.getValues());
                getassociatedResourcesFromTree(compositeValue.getValues());
            }
            else return associatedResources;
        }
        associatedResources.addAll(resouces);
        return associatedResources;

    }

    private String getRDFNodeValue(RDFNode rdfNode){
        String rdfNodeValue = "";
        if(rdfNode!=null) {
            if (rdfNode.isLiteral()) {
                rdfNodeValue = ((Literal) rdfNode).getString();
                if(rdfNodeValue.length()>50){
                    rdfNodeValue = rdfNodeValue.substring(0, 50)+ "...";
                }
            }
            else if (rdfNode.isResource()) {
                rdfNodeValue = ((Resource) rdfNode).getURI();
            }
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
