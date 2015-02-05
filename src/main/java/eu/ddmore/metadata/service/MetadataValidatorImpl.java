package eu.ddmore.metadata.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 14:38
 */
public class MetadataValidatorImpl implements MetadataValidator{

    private MetadataMap metadataMap;

    public boolean ddmoreCertified(){
        if(metadataMap.getPropertiesMap().containsValue("")){
            return false;
        }
        return true;
    }

    public String validationReport(){
        StringBuffer validationReport = new StringBuffer("Validation Report");
        validationReport.append("\n");
        if(ddmoreCertified()){
            validationReport.append("This is a valid DDMoRe certified metadata document");
        }
        else {
            validationReport.append("This is not valid DDMoRe certified metadata document. Please provide the following fields.");
            validationReport.append("\n");
            Iterator it = metadataMap.getPropertiesMap().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                if (pairs.getValue().toString().isEmpty())
                    validationReport.append("Property " + pairs.getKey() + " is empty.\n");
            }
        }
        return validationReport.toString();
    }

    public void setMetadataMap(MetadataMap metadataMap) {
        this.metadataMap = metadataMap;
    }
}
