package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 13/03/2015
 *         Time: 12:13
 */
public interface MetadataMap {
    public HashMap<Resource, ArrayList<Property>> getPropertiesMap();
    public ArrayList<Property> getAssociatedProperties(Resource resource);
    public ArrayList<Resource> getAssociatedResources(Property property);
}
