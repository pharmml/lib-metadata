package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 17/03/2015
 *         Time: 15:24
 */
public class MetadataStatement {

    private Resource subject;
    private Property property;
    private RDFNode rdfNode;

    private int validationLevel;

    public MetadataStatement(Resource subject, Property property, RDFNode rdfNode, int validationLevel) {
        this.subject = subject;
        this.property = property;
        this.rdfNode = rdfNode;
        this.validationLevel = validationLevel;
    }

    public Resource getSubject() {
        return subject;
    }

    public Property getProperty() {
        return property;
    }

    public RDFNode getRdfNode() {
        return rdfNode;
    }

    public int getValidationLevel() {
        return validationLevel;
    }
}
