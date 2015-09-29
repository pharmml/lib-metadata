package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Literal;
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

    public String validationMessage(){
        String validationStatement = "";
        switch (validationLevel) {
            case 0:
                validationStatement = "Property " + property + " is empty.";
                break;
            case 1:
                String rdfNodeValue = "";
                if(rdfNode.isLiteral())
                    rdfNodeValue = ((Literal)rdfNode).getString();
                if(rdfNode.isResource())
                    rdfNodeValue = ((Resource)rdfNode).getURI();

                validationStatement = "Resource " + subject + " has a Property " + property + " has a value of "+ rdfNodeValue + " which is not one of the allowed values .";
                break;
        }
        return validationStatement;
    }
}
