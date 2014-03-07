package javaposse.jobdsl.plugin;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Plugin;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.View;
import hudson.util.VersionNumber;
import javaposse.jobdsl.dsl.AbstractJobManagement;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.ConfigurationMissingException;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.NameNotProvidedException;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hudson.model.View.createViewFromXML;
import static hudson.security.ACL.SYSTEM;

/**
 * Manages Jenkins Jobs, providing facilities to retrieve and create / update.
 */
public final class JenkinsJobManagement extends AbstractJobManagement {
    static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    EnvVars envVars;
    Set<GeneratedJob> modifiedJobs;
    AbstractBuild<?, ?> build;

    JenkinsJobManagement() {
        super();
        envVars = new EnvVars();
        modifiedJobs = Sets.newLinkedHashSet();
    }

    public JenkinsJobManagement(PrintStream outputLogger, EnvVars envVars, AbstractBuild<?, ?> build) {
        super(outputLogger);
        this.envVars = envVars;
        this.modifiedJobs = Sets.newLinkedHashSet();
        this.build = build;
    }

    @Override
    public String getConfig(String jobName) throws JobConfigurationNotFoundException {
        LOGGER.log(Level.INFO, String.format("Getting config for Job %s", jobName));
        String xml;

        if (jobName.isEmpty()) {
            throw new JobConfigurationNotFoundException(jobName);
        }

        try {
            xml = lookupJob(jobName);
        } catch (IOException ioex) {
            LOGGER.log(Level.WARNING, String.format("Named Job Config not found: %s", jobName));
            throw new JobConfigurationNotFoundException(jobName);
        }

        LOGGER.log(Level.FINE, String.format("Job config %s", xml));
        return xml;
    }

    /**
     * TODO cache the <jobName,config> and then let the calling method collect the tuples, so they can be saved at once. Maybe even connect to their template
     */
    @Override
    public boolean createOrUpdateConfig(String fullJobName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {

        LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", fullJobName));
        boolean created = false;

        validateUpdateArgs(fullJobName, config);

        AbstractProject<?,?> project = (AbstractProject<?,?>) Jenkins.getInstance().getItemByFullName(fullJobName);
        String jobName = getJobNameFromFullName(fullJobName);
        Jenkins.checkGoodName(jobName);

        if (project == null) {
            created = createNewJob(fullJobName, config);
        } else if (!ignoreExisting) {
            created = updateExistingJob(project, config);
        }
        return created;
    }

    @Override
    public void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config);
        Jenkins.checkGoodName(viewName);
        try {
            InputStream inputStream = new ByteArrayInputStream(config.getBytes("UTF-8"));

            Jenkins jenkins = Jenkins.getInstance();
            View view = jenkins.getView(viewName);
            if (view == null) {
                jenkins.addView(createViewFromXML(viewName, inputStream));
            } else if (!ignoreExisting) {
                view.updateByXml(new StreamSource(inputStream));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.format("Error writing config for new view %s.", viewName), e);
        }
    }

    @Override
    public Map<String, String> getParameters() {
        return envVars;
    }

    @Override
    public String getCredentialsId(String credentialsDescription) {
        Jenkins jenkins = Jenkins.getInstance();
        Plugin credentialsPlugin = jenkins.getPlugin("credentials");
        if (credentialsPlugin != null && !credentialsPlugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber("1.6"))) {
            for (CredentialsProvider credentialsProvider : jenkins.getExtensionList(CredentialsProvider.class)) {
                for (StandardCredentials credentials : credentialsProvider.getCredentials(StandardCredentials.class, jenkins, SYSTEM)) {
                    if (credentials.getDescription().equals(credentialsDescription) || credentials.getId().equals(credentialsDescription)) {
                        return credentials.getId();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void queueJob(String jobName) throws NameNotProvidedException {
        validateNameArg(jobName);

        AbstractProject<?,?> project = (AbstractProject<?,?>) Jenkins.getInstance().getItemByFullName(jobName);

        if(build != null && build instanceof Run) {
            Run run = (Run) build;
            LOGGER.log(Level.INFO, String.format("Scheduling build of %s from %s", jobName, run.getParent().getName()));
            project.scheduleBuild(new Cause.UpstreamCause(run));
        } else {
            LOGGER.log(Level.INFO, String.format("Scheduling build of %s", jobName));
            project.scheduleBuild(new Cause.UserCause());
        }
    }


    @Override
    public InputStream streamFileInWorkspace(String relLocation) throws IOException {
        FilePath filePath = locateValidFileInWorkspace(relLocation);
        return filePath.read();
    }

    @Override
    public String readFileInWorkspace(String relLocation) throws IOException {
        FilePath filePath = locateValidFileInWorkspace(relLocation);
        return filePath.readToString();
    }

    private FilePath locateValidFileInWorkspace(String relLocation) throws IOException {
        FilePath filePath = build.getWorkspace().child(relLocation);
        try {
            if (!filePath.exists()) {
                throw new IllegalStateException("File does not exists");
            }
        } catch(InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        return filePath;
    }

    private String lookupJob(String jobName) throws IOException {
        LOGGER.log(Level.FINE, String.format("Looking up Job %s", jobName));
        String jobXml = "";

        AbstractProject<?,?> project = (AbstractProject<?,?>) Jenkins.getInstance().getItemByFullName(jobName);
        if (project != null) {
            XmlFile xmlFile = project.getConfigFile();
            jobXml = xmlFile.asString();
        } else {
            LOGGER.log(Level.WARNING, String.format("No Job called %s could be found.", jobName));
            throw new IOException(String.format("No Job called %s could be found.", jobName));

        }

        LOGGER.log(Level.FINE, String.format("Looked up Job with config %s", jobXml));
        return jobXml;
    }

    private boolean updateExistingJob(AbstractProject<?, ?> project, String config) {
        boolean created;

        // Leverage XMLUnit to perform diffs
        Diff diff;
        try {
            String oldJob = project.getConfigFile().asString();
            diff = XMLUnit.compareXML(oldJob, config);
            if (diff.similar()) {
                LOGGER.log(Level.FINE, String.format("Project %s is identical", project.getName()));
                return false;
            }
        } catch (Exception e) {
            // It's not a big deal if we can't diff, we'll just move on
            LOGGER.warning(e.getMessage());
        }

        // TODO Perform comparison between old and new, and print to console
        // TODO Print out, for posterity, what the user might have changed, in the format of the DSL

        LOGGER.log(Level.FINE, String.format("Updating project %s as %s", project.getName(), config));
        StreamSource streamSource = new StreamSource(new StringReader(config)); // TODO use real xmlReader
        try {
            project.updateByXml(streamSource);
            created = true;
        } catch (IOException ioex) {
            LOGGER.log(Level.WARNING, String.format("Error writing updated project to file."), ioex);
            created = false;
        }
        return created;
    }

    // TODO Tag projects as created by us, so that we can intelligently delete them and prevent multiple jobs editing Projects
    private boolean createNewJob(String fullJobName, String config) {
        LOGGER.log(Level.FINE, String.format("Creating project as %s", config));
        boolean created;

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));  // TODO confirm that we're using UTF-8

            ModifiableTopLevelItemGroup ctx = getContextFromFullName(fullJobName);
            String jobName = getJobNameFromFullName(fullJobName);
            ctx.createProjectFromXML(jobName, is);

            created = true;
        } catch (UnsupportedEncodingException ueex) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
            created = false;
        } catch (IOException ioex) {
            LOGGER.log(Level.WARNING, String.format("Error writing config for new job %s.", fullJobName), ioex);
            created = false;
        }
        return created;
    }

    private static ModifiableTopLevelItemGroup getContextFromFullName(String fullName) {
        int i = fullName.lastIndexOf('/');
        Jenkins jenkins = Jenkins.getInstance();
        ModifiableTopLevelItemGroup ctx = jenkins;
        if (i > 0) {
            String contextName = fullName.substring(0, i);
            Item contextItem = jenkins.getItemByFullName(contextName);
            if (contextItem instanceof ModifiableTopLevelItemGroup) {
                ctx = (ModifiableTopLevelItemGroup) contextItem;
            }
        }
        return ctx;
    }

    private static String getJobNameFromFullName(String fullName) {
        int i = fullName.lastIndexOf('/');
        return i > 0 ? fullName.substring(i+1) : fullName;
    }

//    @SuppressWarnings("rawtypes")
//    public Collection<AbstractProject> getJobsByName(final Set<String> names) {
//        return Collections2.filter(Jenkins.getInstance().getProjects(), new Predicate<AbstractProject>() {
//            @Override public boolean apply(AbstractProject project) {
//                return names.contains(project.getName());
//            }
//        });
//    }
//
//    public Collection<AbstractProject> getJobsByGeneratedJobs(final Set<GeneratedJob> generatedJobs) {
//        Set<String> jobNames = Sets.newLinkedHashSet(Collections2.transform(generatedJobs, new ExtractTemplate()));
//        return getJobsByName(jobNames);
//    }

    public static Set<String> getTemplates(Collection<GeneratedJob> jobs) {
        return Sets.newLinkedHashSet(Collections2.filter(Collections2.transform(jobs, new ExtractTemplate()), Predicates.notNull()));
    }

    public static class ExtractJobName implements Function<GeneratedJob, String> {
        @Override public String apply(GeneratedJob input) {
            return input.getJobName();
        }
    }

    public static class ExtractTemplate implements Function<GeneratedJob, String> {
        @Override public String apply(GeneratedJob input) {
            return input.getTemplateName();
        }
    }
}