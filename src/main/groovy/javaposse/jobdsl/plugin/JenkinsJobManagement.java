package javaposse.jobdsl.plugin;

import hudson.XmlFile;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javaposse.jobdsl.dsl.JobManagement;

import javax.xml.transform.stream.StreamSource;

import jenkins.model.Jenkins;

public final class JenkinsJobManagement implements JobManagement {
    Jenkins jenkins = Jenkins.getInstance();
    static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    Set<String> referencedTemplates;

    public JenkinsJobManagement() {
        referencedTemplates = new HashSet<String>();
    }

    @Override
    public String getConfig(String jobName) throws IOException {
        LOGGER.log(Level.INFO, String.format("Getting config for %s", jobName));
        referencedTemplates.add(jobName); // assumes all getConfigs are templates, we might to do a diff on current jobs before update them

        AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
        XmlFile xmlFile = project.getConfigFile();
        String xml = xmlFile.asString();
        LOGGER.log(Level.FINE, String.format("Job config %s", xml));
        return xml;
    }

    /**
     * TODO cache the <jobName,config> and then let the calling method collect the tuples, so they can be saved at once.
     */
    @Override
    public void createOrUpdateConfig(String jobName, String config) throws IOException {
        LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", jobName));
        AbstractProject<?,?> project = (AbstractProject<?,?>) jenkins.getItemByFullName(jobName);
        if (project == null) {
            // Creating project
            LOGGER.log(Level.FINE, String.format("Creating project as %s", config));
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));  // TODO confirm that we're using UTF-8
            TopLevelItem item = jenkins.createProjectFromXML(jobName, is);
        } else {
            LOGGER.log(Level.FINE, String.format("Updating project as %s", config));
            StreamSource streamSource = new StreamSource(new StringReader(config)); // TODO use real xmlReader
            project.updateByXml(streamSource);
        }
    }
}