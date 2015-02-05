package eu.ddmore.metadata.service;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 04/02/2015
 *         Time: 10:06
 */
public interface MetadataReader {
    public MetadataMap metadataRead(File file);
}
