package javaposse.jobdsl.plugin;

import hudson.XmlFile;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.JobManagement;

import javax.xml.transform.stream.StreamSource;

import com.google.common.collect.Sets;

import jenkins.model.Jenkins;

public final class JenkinsJobManagement implements JobManagement {
    Jenkins jenkins = Jenkins.getInstance();
    static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    Set<GeneratedJob> modifiedJobs;

    public JenkinsJobManagement() {
        modifiedJobs = Sets.newHashSet();
    }

    @Override
    public String getConfig(String jobName) throws JobConfigurationNotFoundException {
        LOGGER.log(Level.INFO, String.format("Getting config for %s", jobName));

        String xml;

        // TODO: This is as ugly as sin, but I think it would be nice to have something like this.
        if (jobName.isEmpty()) {
            xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project>\n\t<actions/>\n\t<description/>\n\t<keepDependencies>false</keepDependencies>\n\t<properties/>\n</project>";
        } else {
            try {
                xml = lookupJob(jobName);
            } catch (IOException ioex) {
                LOGGER.log(Level.WARNING, "Named Job Config not found: %s", jobName);
                throw new JobConfigurationNotFoundException(jobName);
            }
        }

        LOGGER.log(Level.FINE, String.format("Job config %s", xml));
        return xml;
    }

    /**
     * TODO cache the <jobName,config> and then let the calling method collect the tuples, so they can be saved at once. Maybe even connect to their template
     */
    @Override
    public boolean createOrUpdateConfig(String jobName, String config) throws IOException {
        LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", jobName));
        AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
        boolean created = false;
        if (project == null) {
            // Creating project
            LOGGER.log(Level.FINE, String.format("Creating project as %s", config));
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));  // TODO confirm that we're using UTF-8
            TopLevelItem item = jenkins.createProjectFromXML(jobName, is);
            created = true;
        } else {
            LOGGER.log(Level.FINE, String.format("Updating project as %s", config));
            // TODO Perform comparison between old and new, and print to console
            // TODO Print out, for posterity, what the user might ahve changed, in the format of the DSL
            // TODO Leverage XMLUnit to perform diffs
            StreamSource streamSource = new StreamSource(new StringReader(config)); // TODO use real xmlReader
            project.updateByXml(streamSource);
        }
        return created;
    }

    private String lookupJob(String jobName) throws IOException {
        AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
        XmlFile xmlFile = project.getConfigFile();
        String xml = xmlFile.asString();
        return xml;
    }
}