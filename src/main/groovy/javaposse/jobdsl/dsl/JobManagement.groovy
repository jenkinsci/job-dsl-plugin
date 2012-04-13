package javaposse.jobdsl.dsl;

import java.io.IOException;

/**
 * Interface to manage jobs, which the DSL needs to do.
 * 
 * @author jryan
 *
 */
public interface JobManagement {
    /**
     * Gets (loads) the job configuration for the Jenkins job with the specified name
     * @param jobName the name of the job to look up
     * @return the job configuration as XML
     * @throws IOException
     */
    String getConfig(String jobName) throws IOException;

    /**
     * Creates or updates the job config for the named Jenkins job with the config provided
     * @param jobName the name of the new / updated job
     * @param config the new / updated job config
     * @throws IOException
     */
    void createOrUpdateConfig(String jobName, String config) throws IOException;
}
