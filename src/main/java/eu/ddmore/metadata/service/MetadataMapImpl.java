package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

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

    public MetadataMapImpl(String propMap, String ontoMap ) {
        org.springframework.core.io.Resource propMapResource = new ClassPathResource(propMap);
        org.springframework.core.io.Resource ontoMapResource = new ClassPathResource(ontoMap);
        try {
            constructPropMap(IOUtils.toString(propMapResource.getInputStream(), "UTF-8"));
            constructOntoMap(IOUtils.toString(ontoMapResource.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void constructPropMap(String propMapFile) {
        try {
            CSVParser csvParser = CSVParser.parse(propMapFile, CSVFormat.DEFAULT);
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

    private void constructOntoMap(String ontoMapFile) {
        try {
            CSVParser csvParser = CSVParser.parse(ontoMapFile,CSVFormat.DEFAULT);
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


