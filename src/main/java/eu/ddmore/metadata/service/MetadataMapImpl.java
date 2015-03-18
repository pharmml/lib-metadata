package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.sun.xml.internal.bind.v2.runtime.property.PropertyFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import static java.nio.charset.Charset.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 11:21
 */
public class MetadataMapImpl implements MetadataMap{
    private HashMap<Resource, ArrayList<Property>> propertiesMap = new HashMap<Resource, ArrayList<Property>>();
    private HashMap<Property, ArrayList<Resource>> ontologyMap = new HashMap<Property, ArrayList<Resource>>();

    public MetadataMapImpl(File propMapFile, File ontoMapFile ) {
        constructPropMap(propMapFile);
        constructOntoMap(ontoMapFile);
    }

    private void constructPropMap(File propMapFile) {
        try {
            CSVParser csvParser = CSVParser.parse(propMapFile,Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : csvParser) {
                Resource resource = ResourceFactory.createResource(csvRecord.get(0));
                Property property = ResourceFactory.createProperty(csvRecord.get(1));

                if(propertiesMap.containsKey(resource)){
                    propertiesMap.get(resource).add(property);
                }
                else {
                    ArrayList<Property> propertyList = new ArrayList<Property>();
                    propertyList.add(property);
                    propertiesMap.put(resource, propertyList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void constructOntoMap(File ontoMapFile) {
        try {
            CSVParser csvParser = CSVParser.parse(ontoMapFile,Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : csvParser) {
                Property property = ResourceFactory.createProperty(csvRecord.get(0));
                Resource resource = ResourceFactory.createResource(csvRecord.get(1));

                if(ontologyMap.containsKey(property)){
                    ontologyMap.get(property).add(resource);
                }
                else {
                    ArrayList<Resource> resourceList = new ArrayList<Resource>();
                    resourceList.add(resource);
                    ontologyMap.put(property, resourceList);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public HashMap<Resource, ArrayList<Property>> getPropertiesMap() {
        return propertiesMap;
    }

    @Override
    public ArrayList<Property> getAssociatedProperties(Resource resource) {
        return propertiesMap.get(resource);
    }

    @Override
    public ArrayList<Resource> getAssociatedResources(Property property) {
        return ontologyMap.get(property);
    }
}


