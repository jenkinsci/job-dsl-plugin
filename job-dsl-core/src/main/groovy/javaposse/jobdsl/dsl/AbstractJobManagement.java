package javaposse.jobdsl.dsl;

import com.google.common.collect.Maps;
import hudson.util.VersionNumber;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Thread.currentThread;
import static org.codehaus.groovy.runtime.StackTraceUtils.isApplicationClass;

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

    @Override
    public String readFileInWorkspace(String jobName, String filePath) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logDeprecationWarning() {
        List<StackTraceElement> stackTrace = getStackTrace();
        String details = getSourceDetails(stackTrace);
        logWarning("%s is deprecated (%s)", stackTrace.get(0).getMethodName(), details);
    }

    @Override
    public VersionNumber getPluginVersion(String pluginShortName) {
        return null;
    }

    @Override
    public Integer getVSphereCloudHash(String name) {
        return null;
    }

    @Override
    public String getMavenSettingsId(String settingsName) {
        return null;
    }

    protected void validateUpdateArgs(String jobName, String config) {
        validateNameArg(jobName);
        validateConfigArg(config);
    }

    protected void validateConfigArg(String config) {
        if (config == null || config.isEmpty()) throw new ConfigurationMissingException();
    }

    protected void validateNameArg(String name) {
        if (name == null || name.isEmpty()) throw new NameNotProvidedException();
    }

    protected static List<StackTraceElement> getStackTrace() {
        List<StackTraceElement> result = newArrayList();
        for (StackTraceElement stackTraceElement : currentThread().getStackTrace()) {
            if (isApplicationClass(stackTraceElement.getClassName())) {
                result.add(stackTraceElement);
            }
        }
        return result.subList(3, result.size());
    }

    protected static String getSourceDetails(List<StackTraceElement> stackTrace) {
        StackTraceElement source = null;
        for (StackTraceElement stackTraceElement : stackTrace) {
            if (!stackTraceElement.getClassName().startsWith("javaposse.jobdsl.")) {
                source = stackTraceElement;
                break;
            }
        }
        String details = "unknown source";
        if (source != null && source.getFileName() != null) {
            details = source.getFileName().matches("script\\d+\\.groovy") ? "DSL script" : source.getFileName();
            if (source.getLineNumber() > 0) {
                details += ", line " + source.getLineNumber();
            }
        }
        return details;
    }

    protected void logWarning(String message, Object... args) {
        getOutputStream().printf("Warning: " + message + "\n", args);
    }
}
