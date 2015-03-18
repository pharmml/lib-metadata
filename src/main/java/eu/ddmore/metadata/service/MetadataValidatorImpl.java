package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;

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

    private MetadataMap metadataMap;
    private Model model;
    private ArrayList<MetadataStatement> metadataStatements = new ArrayList<MetadataStatement>();

    public MetadataValidatorImpl(Model model, MetadataMap metadataMap) {
        this.model = model;
        this.metadataMap = metadataMap;
    }

    @Override
    public boolean ddmoreCertified(File file) {
        model.read(file.toURI().toString());

        HashMap<Resource, ArrayList<Property>> propertiesMap = metadataMap.getPropertiesMap();
        for (Map.Entry<Resource, ArrayList<Property>> resourceEntry : propertiesMap.entrySet()) {
            Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            ResIterator resIterator = model.listSubjectsWithProperty(typeProperty,resourceEntry.getKey());
            if(resIterator!=null) {
                while (resIterator.hasNext()) {
                    Resource resource = resIterator.next();
                    for (Property property : resourceEntry.getValue()) {
                        StmtIterator stmtIterator = resource.listProperties(property);
                        if (stmtIterator!=null){
                            if(!stmtIterator.hasNext()){
                                metadataStatements.add(new MetadataStatement(resourceEntry.getKey(),property,null,0));
                            }

                            while (stmtIterator.hasNext()){
                                Statement statement = stmtIterator.next();
                                int validationLevel = validationLevel(property,statement.getObject());
                                if(validationLevel != -1){
                                    metadataStatements.add(new MetadataStatement(resourceEntry.getKey(),property,statement.getObject(),validationLevel));
                                }
                            }
                        }
                    }
                }
            }
            else{
                metadataStatements.add(new MetadataStatement(resourceEntry.getKey(),typeProperty,null,0));
            }
        }
        return false;
    }




    public int validationLevel(Property property, RDFNode rdfNode){
        ArrayList<Resource> associatedResources = metadataMap.getAssociatedResources(property);
        if(associatedResources==null){
            return -1;
        }
        else{
            if(rdfNode.isLiteral()){
                return 1;
            }
            else {
                Resource givenOntoResource = rdfNode.asResource();
                for(Resource validResource: associatedResources){
                    if(validResource.equals(givenOntoResource)){
                        return -1;
                    }
                }
                return 1;
            }
        }
    }

    public String validationReport(){
        StringBuffer validationReport = new StringBuffer("Validation Report");
        validationReport.append("\n");
        if(metadataStatements.isEmpty()){
            validationReport.append("This is a valid DDMoRe certified metadata document");
        }
        else {
            validationReport.append("This is not valid DDMoRe certified metadata document. Please provide the following fields.");
            validationReport.append("\n");
            for(MetadataStatement metadataStatement : metadataStatements) {
                switch (metadataStatement.getValidationLevel()) {
                    case 0:
                        validationReport.append("Property " + metadataStatement.getProperty() + " is empty.\n");
                        break;
                    case 1:
                        String rdfNodeValue = "";
                        if(metadataStatement.getRdfNode().isLiteral())
                            rdfNodeValue = metadataStatement.getRdfNode().asLiteral().getString();
                        if(metadataStatement.getRdfNode().isResource())
                            rdfNodeValue = metadataStatement.getRdfNode().asResource().getURI();

                        validationReport.append("Resouce " + metadataStatement.getSubject() + " has a Property " + metadataStatement.getProperty() + " has a value of "+ rdfNodeValue + " which is not one of the allowed values .\n");

                        break;
                }
            }

        }
        return validationReport.toString();
    }

}
