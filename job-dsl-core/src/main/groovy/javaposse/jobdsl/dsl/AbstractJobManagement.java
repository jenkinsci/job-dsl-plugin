package javaposse.jobdsl.dsl;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

/**
 * Abstract version of JobManagement to minimize impact on future API changes
 */
public abstract class AbstractJobManagement implements JobManagement {
    protected PrintStream out;

    protected AbstractJobManagement(PrintStream out) {
        this.out = out;
    }

    protected AbstractJobManagement() {
        this(System.out);
    }

    @Override
    public PrintStream getOutputStream() {
        return out;
    }

    /**
     * Map if variables that should be available to the script.
     */
     @Override
     public Map<String, String> getParameters() {
        return Maps.newHashMap();
    }

    @Override
    public String getCredentialsId(String credentialsDescription) {
        return null;
    }

    @Override
    public void queueJob(String jobName) throws NameNotProvidedException {
        validateNameArg(jobName);
    }

    @Override
    public InputStream streamFileInWorkspace(String filePath) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String readFileInWorkspace(String filePath) throws IOException {
        throw new UnsupportedOperationException();
    }

    protected void validateUpdateArgs(String jobName, String config) {
        validateNameArg(jobName);
        validateConfigArg(config);
    }

    protected void validateUpdateArgs(String jobName, JobConfig config) {
        validateNameArg(jobName);
        validateConfigArg(config);
    }

    protected void validateConfigArg(String config) {
        if (config == null || config.isEmpty()) throw new ConfigurationMissingException();
    }

    protected void validateConfigArg(JobConfig config) {
        if (config == null || !config.isValid()) throw new ConfigurationMissingException();
    }

    protected void validateNameArg(String name) {
        if (name == null || name.isEmpty()) throw new NameNotProvidedException();
    }

}
