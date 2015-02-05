package eu.ddmore.metadata.service;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 11:21
 */
public class MetadataMap {
    public static String PROP_DOWNLOADURL = "http://www.w3.org/ns/dcat#downloadURL";
    public static String PROP_CITESASAUTHORITY = "http://purl.org/spar/cito/citesAsAuthority";
    public static String PROP_DESCRIPTION = "http://purl.org/dc/terms/description";
    public static String PROP_TITLE = "http://purl.org/dc/terms/title";
    public static String PROP_PUBLISHER = "http://purl.org/dc/terms/publisher";
    public static String PROP_CREATOR = "http://purl.org/dc/terms/creator";

    private HashMap<String, String> propertiesMap = new HashMap<String, String>();

    public MetadataMap() {
        propertiesMap.put(PROP_DOWNLOADURL, "");
        propertiesMap.put(PROP_CITESASAUTHORITY, "");
        propertiesMap.put(PROP_DESCRIPTION, "");
        propertiesMap.put(PROP_TITLE, "");
        propertiesMap.put(PROP_PUBLISHER, "");
        propertiesMap.put(PROP_CREATOR, "");
    }

    public void updatePropertiesMapValue(String property, String value){
        propertiesMap.put(property,value);
    }

    public HashMap<String, String> getPropertiesMap() {
        return propertiesMap;
    }

}


