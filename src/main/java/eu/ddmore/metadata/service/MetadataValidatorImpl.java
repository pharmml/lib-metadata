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
 * @author Mihai Glonț
 *         Date: 02/04/2016
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

    private static final String MODEL_RESOURCE_NS = "http://www.pharmml.org/ontology/PharmMLO_0000001";

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
        List<Section> sections = new ArrayList<Section>();
        getAllSections(metadataInfoService.getAllPopulatedRootSections(), sections);
        HashMap<String, List<eu.ddmore.metadata.api.domain.properties.Property>> requiredProperties = getRequiredProperties(sections);

        if (!requiredProperties.isEmpty()) {
            for (Map.Entry <String, List<eu.ddmore.metadata.api.domain.properties.Property>> entry :requiredProperties.entrySet()) {
                String conceptIdURL = entry.getKey();
                List propertyList = entry.getValue();

                //correct element annotation mapping
                Resource resource = getAnnotatedResouce(conceptIdURL);

                //hardcoded for the current incorrect approach as requested
                //Resource resource = getAnnotatedResouce(MODEL_RESOURCE_NS);
                validate(propertyList, resource);

            }

        }
    }

    private Resource getAnnotatedResouce(String url) {
        Resource resourceEntry = ResourceFactory.createResource(url);
        Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
        ResIterator resIterator = model.listSubjectsWithProperty(typeProperty, resourceEntry);

        if(!resIterator.hasNext()){
            return null;
        }

        return resIterator.nextResource();

    }

    private void getAllSections(List<Section> sections, List<Section> sectionsToValidate){
        for (Section section : sections) {
            if (!section.isEnabled()) {
                // skip this section and any of its children if the disabled flag is set
                if (IS_DEBUG_ENABLED) {
                    final String sectionLabel = section.getSectionLabel();
                    logger.debug("Skipping disabled section " + sectionLabel);
                }
                continue;
            }
            if(section instanceof CompositeSection){
                getAllSections(((CompositeSection)section).getSections(), sectionsToValidate);
            }
            else {
                if (IS_DEBUG_ENABLED) {
                    final String sectionLabel = section.getSectionLabel();
                    logger.debug("Adding required section " + sectionLabel);
                }
                sectionsToValidate.add(section);
            }
        }


    }

    private void validate(List<eu.ddmore.metadata.api.domain.properties.Property> requiredProperties, Resource resource)  {
        /* Validation of required fields, range type, associated resources */
        int count = 0;
        for (eu.ddmore.metadata.api.domain.properties.Property requiredProperty : requiredProperties) {
            if(resource==null){
                validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.EMPTY, requiredProperty.getPropertyId().getUri()));
                continue;
            }
                Property property = ResourceFactory.createProperty(requiredProperty.getPropertyId().getUri());
                StmtIterator stmtIterator = resource.listProperties(property);
            if (IS_DEBUG_ENABLED) {
                StringBuilder msg = new StringBuilder();
                final String pLabel = requiredProperty.getPropertyId().getLabel();
                msg.append(++count).append(". Property ").append(pLabel);
                logger.debug(msg.toString());
            }
            if (stmtIterator!=null){
                if(!stmtIterator.hasNext()){
                    validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.EMPTY,
                            requiredProperty.getPropertyId().getUri()));
                }
                while (stmtIterator.hasNext()){
                    Statement statement = stmtIterator.nextStatement();
                    boolean isValid = isValid(requiredProperty, statement.getObject());
                    if(!isValid){
                        validationHandler.addValidationError(new ValidationError(ValidationErrorStatus.INVALID,
                                requiredProperty.getPropertyId().getUri(), getRDFNodeValue(statement.getObject())));
                    }
                }
            }
        }
    }

    private HashMap <String, List<eu.ddmore.metadata.api.domain.properties.Property>> getRequiredProperties(List<Section> sections){
        HashMap <String, List<eu.ddmore.metadata.api.domain.properties.Property>> requiredProperties = new HashMap <String, List<eu.ddmore.metadata.api.domain.properties.Property>>();

        for(Section section: sections) {
            for(eu.ddmore.metadata.api.domain.properties.Property property: metadataInfoService.findPropertiesForSection(section)){
                if(property.isRequired()){
                    List<eu.ddmore.metadata.api.domain.properties.Property> propertyList = requiredProperties.get(property.getConceptId().getUri());
                    if(propertyList==null) {
                        propertyList = new ArrayList<eu.ddmore.metadata.api.domain.properties.Property>();
                        requiredProperties.put(property.getConceptId().getUri(), propertyList);
                    }
                    propertyList.add(property);
                }
            }
        }
        return requiredProperties;

    }

    public boolean isValid(eu.ddmore.metadata.api.domain.properties.Property property, RDFNode rdfNode){
        List<Value> associatedResources = null;
        final ValueSetType RANGE_TYPE = property.getValueSetType();
        switch(RANGE_TYPE) {
            case TEXT:
                return true;
            case LIST:
                associatedResources = metadataInfoService.findValuesForProperty(property);
                break;
            case TREE:
                associatedResources = getassociatedResourcesFromTree(
                        metadataInfoService.findValuesForProperty(property));
                break;
            case ONTOLOGY:
                if (rdfNode.isResource()) {
                    List<OntologySource> sources =
                            metadataInfoService.findOntologyResourcesForProperty(property);
                    return resourceExistInOLS(sources, ((Resource) rdfNode).getURI());
                } else {
                    //if property is an ontology, rdfNode must be a resource
                    return false;
                }
            case TEXTLIST:
                return isTextValueWithinRange(rdfNode, property);
            default:
                throw new IllegalArgumentException("Unexpected value set type " + RANGE_TYPE.toString());
        }

        if(associatedResources == null){
            return true;
        }
        else {
            if (rdfNode.isLiteral()){
                return false;
            } else if (rdfNode.isResource()) {
                Resource givenOntoResource = (Resource)rdfNode;
                for (Value validResource: associatedResources) {
                    if (validResource.getValueId().getUri().equals(givenOntoResource.getURI())) {
                        return true;
                    }
                }
                return false;
            }
            else
                return false;
        }
    }

    /*
     * Simple method to check whether a textual value is within a property's expected range.
     *
     * @param value the literal value for which to perform the test.
     * @param property the property whose range of accepted values should include the supplied value.
     */
    private boolean isTextValueWithinRange(RDFNode value,
            eu.ddmore.metadata.api.domain.properties.Property property) {
        if (!value.isLiteral()) {
            if (logger.isWarnEnabled()) {
                final String uri = ((Resource) value).getURI();
                final String propertyLabel = property.getPropertyId().getLabel();
                final int size = uri.length() + propertyLabel.length() + 45;
                StringBuilder msg = new StringBuilder(size);
                msg.append("Property ").append(propertyLabel).append(
                        " cannot contain non-textual value ").append(uri);
                logger.warn(msg.toString());
            }
            return false;
        }
        final String actualValue = ((Literal) value).getString();
        final List<Value> range = metadataInfoService.findValuesForProperty(property);
        for (Value expected: range) {
            String thisLabel = expected.getValueId().getLabel();
            if (thisLabel.equals(actualValue)) {
                return true;
            }
        }
        return false;
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
