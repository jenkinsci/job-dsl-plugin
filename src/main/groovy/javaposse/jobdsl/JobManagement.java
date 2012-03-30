package javaposse.jobdsl;

import java.io.IOException;

/**
 * Interface to manage jobs, which the DSL needs to do.
 * 
 * @author jryan
 *
 */
public interface JobManagement {

    String getConfig(String jobName) throws IOException;
    void createOrUpdateConfig(String jobName, String config) throws IOException;
}
