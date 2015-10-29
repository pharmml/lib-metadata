package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFFormat;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 28/09/2015
 *         Time: 09:40
 */
public interface MetadataWriter {
    void generateTriple(String subject, String predicate, String object);
    void generateLiteralTriple(String subject, String predicate, Object object);
    void writeRDFModel(String fileName, RDFFormat format );
    Model getModel();
}
