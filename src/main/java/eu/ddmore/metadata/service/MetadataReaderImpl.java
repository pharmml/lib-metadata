package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.*;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 10:05
 */
public class MetadataReaderImpl implements MetadataReader{
    private Model model;
    private MetadataMap metadataMap;

    public MetadataReaderImpl(Model model, MetadataMap metadataMap) {
        this.model = model;
        this.metadataMap = metadataMap;
    }

    public MetadataMap metadataRead(File metadataFile){
        model.read(metadataFile.toURI().toString());
        return createMetadata();
    }

    private MetadataMap createMetadata(){
        StmtIterator stmtIterator = model.listStatements();
        while (stmtIterator.hasNext()){
            Statement statement = stmtIterator.next();
            metadataMap.updatePropertiesMapValue(statement.getPredicate().toString(),statement.getObject().toString());
        }
        return metadataMap;
    }
}
