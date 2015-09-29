package eu.ddmore.metadata.service;

import com.github.jsonldjava.core.RDFDataset;
import com.hp.hpl.jena.rdf.model.*;
import org.apache.jena.riot.RDFFormat;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 28/09/2015
 *         Time: 09:40
 */
public class MetadataWriterImpl implements MetadataWriter {
    Model model = ModelFactory.createDefaultModel();

    public void generateTriple(String subject, String predicate, String object) {
        Resource s = model.createResource(subject);
        Property p = model.createProperty(predicate);
        Resource o = model.createResource(object);
        model.add(s, p, o);
    }

    public void generateLiteralTriple(String subject, String predicate, Object object) {
        Resource s = model.createResource(subject);
        Property p = model.createProperty(predicate);
        Literal o =model.createTypedLiteral(object);

        model.add(s, p, o);
    }

    public void writeRDFModel(String fileName, RDFFormat format ){
        try {
            FileWriter fileWriter = new FileWriter( fileName );
            model.write(fileWriter,format.getLang().getLabel());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Model getModel() {
        return model;
    }
}
