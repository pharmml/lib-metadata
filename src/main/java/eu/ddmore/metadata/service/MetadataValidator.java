package eu.ddmore.metadata.service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 14:37
 */
public interface MetadataValidator {
    public boolean ddmoreCertified();
    public String validationReport();
    public void setMetadataMap(MetadataMap metadataMap);
}
