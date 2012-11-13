package javaposse.jobdsl.plugin;

import groovy.util.ResourceConnector;
import groovy.util.ResourceException;
import hudson.FilePath;
import hudson.model.AbstractBuild;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Implements Groovy's ResourceConnector
 */
public class JenkinsResourceConnector implements ResourceConnector {
    AbstractBuild<?, ?> build;

    public JenkinsResourceConnector(AbstractBuild<?, ?> build) {
        this.build = build;
    }

  static class StringUrlConnection extends URLConnection {

        String internal;
        long created;

        public StringUrlConnection(String internal) {
            super(null);
            this.internal = internal;
            created = System.currentTimeMillis();
        }

        @Override
        public long getLastModified() {
            return created;
        }

        @Override
        public void connect() throws IOException {
            // Required abstract method, not sure if it's needed
        }

        @Override
        public URL getURL() {
            try {
                return new URL("string://" + internal.hashCode());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                // We don't really have a fall back plan
                return null;
            }
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(internal.getBytes());
        }
    }

    @Override
    public URLConnection getResourceConnection(String target) throws ResourceException {
        FilePath targetPath = build.getModuleRoot().child(target);
        try {
            if (!targetPath.exists()) {
                targetPath = build.getWorkspace().child(target);
                if (!targetPath.exists()) {
                    throw new FileNotFoundException("Unable to find DSL script at " + target);
                }
            }
            String dslBody = targetPath.readToString();
            return new StringUrlConnection(dslBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
