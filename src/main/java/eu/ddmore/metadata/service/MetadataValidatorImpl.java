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
    private ArrayList<MetadataStatement> metadataStatements;

    public MetadataValidatorImpl(MetadataMap metadataMap) {
        this.metadataMap = metadataMap;
    }

    public boolean ddmoreCertified(String url) {
        metadataStatements = new ArrayList<MetadataStatement>();
        Model model = ModelFactory.createDefaultModel();
        model.read(url);
        HashMap<Resource, ArrayList<Property>> propertiesMap = metadataMap.getPropertiesMap();
        for (Map.Entry<Resource, ArrayList<Property>> resourceEntry : propertiesMap.entrySet()) {
            Property typeProperty = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
            ResIterator resIterator = model.listSubjectsWithProperty(typeProperty,resourceEntry.getKey());
            if(resIterator!=null) {
                while (resIterator.hasNext()) {
                    Resource resource = resIterator.nextResource();
                    for (Property property : resourceEntry.getValue()) {
                        StmtIterator stmtIterator = resource.listProperties(property);
                        if (stmtIterator!=null){
                            if(!stmtIterator.hasNext()){
                                metadataStatements.add(new MetadataStatement(resourceEntry.getKey(),property,null,0));
                            }

                            while (stmtIterator.hasNext()){
                                Statement statement = stmtIterator.nextStatement();
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
            else if(rdfNode.isResource()){
                Resource givenOntoResource = (Resource)rdfNode;
                for(Resource validResource: associatedResources){
                    if(validResource.equals(givenOntoResource)){
                        return -1;
                    }
                }
                return 1;
            }
            else
                return 1;
        }
    }

    public ArrayList<MetadataStatement> getMetadataStatements() {
        return metadataStatements;
    }

}
