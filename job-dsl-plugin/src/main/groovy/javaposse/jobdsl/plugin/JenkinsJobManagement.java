package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
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
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.util.VersionNumber;
import javaposse.jobdsl.dsl.AbstractJobManagement;
import javaposse.jobdsl.dsl.ConfigurationMissingException;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.NameNotProvidedException;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.transform.Source;
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

    @Override
    public boolean createOrUpdateConfig(String fullItemName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {

        LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", fullItemName));
        boolean created = false;

        validateUpdateArgs(fullItemName, config);

        AbstractItem item = (AbstractItem) Jenkins.getInstance().getItemByFullName(fullItemName);
        String jobName = getItemNameFromFullName(fullItemName);
        Jenkins.checkGoodName(jobName);

        if (item == null) {
            created = createNewItem(fullItemName, config);
        } else if (!ignoreExisting) {
            created = updateExistingItem(item, config);
        }
        return created;
    }

    @Override
    public void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config);
        String viewBaseName = getItemNameFromFullName(viewName);
        Jenkins.checkGoodName(viewBaseName);
        try {
            InputStream inputStream = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ViewGroup viewGroup = getViewGroup(viewName);
            View view = viewGroup.getView(viewBaseName);
            if (view == null) {
                if (viewGroup instanceof Jenkins) {
                    ((Jenkins) viewGroup).addView(createViewFromXML(viewBaseName, inputStream));
                } else if (viewGroup instanceof Folder) {
                    ((Folder) viewGroup).addView(createViewFromXML(viewBaseName, inputStream));
                } else {
                    LOGGER.log(Level.WARNING, String.format("Could not create view within %s", viewGroup.getClass()));
                }
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
            project.scheduleBuild(new Cause.UserIdCause());
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
                String path = filePath.getRemote();
                throw new IllegalStateException(String.format("File %s does not exist in workspace.", path));
            }
        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        }
        return filePath;
    }

    private String lookupJob(String jobName) throws IOException {
        LOGGER.log(Level.FINE, String.format("Looking up item %s", jobName));
        String jobXml = "";

        AbstractItem item = (AbstractItem) Jenkins.getInstance().getItemByFullName(jobName);
        if (item != null) {
            XmlFile xmlFile = item.getConfigFile();
            jobXml = xmlFile.asString();
        } else {
            LOGGER.log(Level.WARNING, String.format("No item called %s could be found.", jobName));
            throw new IOException(String.format("No item called %s could be found.", jobName));

        }

        LOGGER.log(Level.FINE, String.format("Looked up item with config %s", jobXml));
        return jobXml;
    }

    private boolean updateExistingItem(AbstractItem item, String config) {
        boolean created;

        // Leverage XMLUnit to perform diffs
        Diff diff;
        try {
            String oldJob = item.getConfigFile().asString();
            diff = XMLUnit.compareXML(oldJob, config);
            if (diff.similar()) {
                LOGGER.log(Level.FINE, String.format("Item %s is identical", item.getName()));
                return false;
            }
        } catch (Exception e) {
            // It's not a big deal if we can't diff, we'll just move on
            LOGGER.warning(e.getMessage());
        }

        LOGGER.log(Level.FINE, String.format("Updating item %s as %s", item.getName(), config));
        Source streamSource = new StreamSource(new StringReader(config));
        try {
            item.updateByXml(streamSource);
            created = true;
        } catch (IOException ioex) {
            LOGGER.log(Level.WARNING, String.format("Error writing updated item to file."), ioex);
            created = false;
        }
        return created;
    }

    private boolean createNewItem(String fullItemName, String config) {
        LOGGER.log(Level.FINE, String.format("Creating item as %s", config));
        boolean created;

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ModifiableTopLevelItemGroup ctx = getContextFromFullName(fullItemName);
            String itemName = getItemNameFromFullName(fullItemName);
            ctx.createProjectFromXML(itemName, is);

            created = true;
        } catch (UnsupportedEncodingException ueex) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
            created = false;
        } catch (IOException ioex) {
            LOGGER.log(Level.WARNING, String.format("Error writing config for new item %s.", fullItemName), ioex);
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

    static String getItemNameFromFullName(String fullName) {
        int i = fullName.lastIndexOf('/');
        return i > 0 ? fullName.substring(i+1) : fullName;
    }

    static ViewGroup getViewGroup(String fullName) {
        Jenkins jenkins = Jenkins.getInstance();
        int i = fullName.lastIndexOf('/');
        return i > 0 ? jenkins.getItemByFullName(fullName.substring(0, i), Folder.class) : jenkins;
    }

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
