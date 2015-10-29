package eu.ddmore.metadata.service;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Sarala Wimalaratne
 *         Date: 02/10/2015
 *         Time: 09:32
 */
public enum ValidationStatus {

    /*Waiting to be approved*/
    APPROVE,

    /*Metadata Approved*/
    APPROVED,

    /*Metada invalid - rejected*/
    REJECTED,

    /*Metadata fields missing*/
    CONDITIONALLY_APPROVED

}
