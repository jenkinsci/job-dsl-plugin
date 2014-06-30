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
import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.ItemGroup;
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

import static hudson.model.Result.UNSTABLE;
import static hudson.model.View.createViewFromXML;
import static hudson.security.ACL.SYSTEM;

/**
 * Manages Jenkins jobs, providing facilities to retrieve and create / update.
 */
public final class JenkinsJobManagement extends AbstractJobManagement {
    private static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    private final EnvVars envVars;
    private final AbstractBuild<?, ?> build;
    private final LookupStrategy lookupStrategy;

    public JenkinsJobManagement(PrintStream outputLogger, EnvVars envVars, AbstractBuild<?, ?> build,
                                LookupStrategy lookupStrategy) {
        super(outputLogger);
        this.envVars = envVars;
        this.build = build;
        this.lookupStrategy = lookupStrategy;
    }

    public JenkinsJobManagement(PrintStream outputLogger, EnvVars envVars, AbstractBuild<?, ?> build) {
        this(outputLogger, envVars, build, LookupStrategy.JENKINS_ROOT);
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
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.format("Named Job Config not found: %s", jobName));
            throw new JobConfigurationNotFoundException(jobName);
        }

        LOGGER.log(Level.FINE, String.format("Job config %s", xml));
        return xml;
    }

    @Override
    public boolean createOrUpdateConfig(String itemName, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {

        LOGGER.log(Level.INFO, String.format("createOrUpdateConfig for %s", itemName));
        boolean created = false;

        validateUpdateArgs(itemName, config);

        AbstractItem item = lookupStrategy.getItem(build.getProject(), itemName, AbstractItem.class);
        String jobName = getItemNameFromPath(itemName);
        Jenkins.checkGoodName(jobName);

        if (item == null) {
            created = createNewItem(itemName, config);
        } else if (!ignoreExisting) {
            created = updateExistingItem(item, config);
        }
        return created;
    }

    @Override
    public void createOrUpdateView(String path, String config, boolean ignoreExisting) {
        validateUpdateArgs(path, config);
        String viewBaseName = getItemNameFromPath(path);
        Jenkins.checkGoodName(viewBaseName);
        try {
            InputStream inputStream = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(build.getProject(), path);
            if (parent instanceof ViewGroup) {
                View view = ((ViewGroup) parent).getView(viewBaseName);
                if (view == null) {
                    if (parent instanceof Jenkins) {
                        ((Jenkins) parent).addView(createViewFromXML(viewBaseName, inputStream));
                    } else if (parent instanceof Folder) {
                        ((Folder) parent).addView(createViewFromXML(viewBaseName, inputStream));
                    } else {
                        LOGGER.log(Level.WARNING, String.format("Could not create view within %s", parent.getClass()));
                    }
                } else if (!ignoreExisting) {
                    view.updateByXml(new StreamSource(inputStream));
                }
            } else {
                LOGGER.log(Level.WARNING, String.format("Could not create view within %s", parent.getClass()));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, String.format("Error writing config for new view %s.", path), e);
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

        BuildableItem project = lookupStrategy.getItem(build.getParent(), jobName, BuildableItem.class);

        LOGGER.log(Level.INFO, String.format("Scheduling build of %s from %s", jobName, build.getParent().getName()));
        project.scheduleBuild(new Cause.UpstreamCause((Run) build));
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

    @Override
    public void requireMinimumPluginVersion(String pluginShortName, String version) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        if (plugin == null) {
            markBuildAsUnstable("plugin '" + pluginShortName + "' needs to be installed");
        } else if (plugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber(version))) {
            markBuildAsUnstable("plugin '" + pluginShortName + "' needs to be updated to version " + version + " or later");
        }
    }

    private void markBuildAsUnstable(String message) {
        getOutputStream().println("Warning: " + message + " (" + getSourceDetails(getStackTrace()) + ")");
        build.setResult(UNSTABLE);
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

        AbstractItem item = lookupStrategy.getItem(build.getProject(), jobName, AbstractItem.class);
        if (item != null) {
            XmlFile xmlFile = item.getConfigFile();
            String jobXml = xmlFile.asString();
            LOGGER.log(Level.FINE, String.format("Looked up item with config %s", jobXml));
            return jobXml;
        } else {
            LOGGER.log(Level.WARNING, String.format("No item called %s could be found.", jobName));
            throw new IOException(String.format("No item called %s could be found.", jobName));
        }
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
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.format("Error writing updated item to file."), e);
            created = false;
        }
        return created;
    }

    private boolean createNewItem(String path, String config) {
        LOGGER.log(Level.FINE, String.format("Creating item as %s", config));
        boolean created = false;

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(build.getProject(), path);
            String itemName = getItemNameFromPath(path);
            if (parent instanceof ModifiableTopLevelItemGroup) {
                ((ModifiableTopLevelItemGroup) parent).createProjectFromXML(itemName, is);
                created = true;
            } else {
                LOGGER.log(Level.WARNING, String.format("Could not create item within %s", parent.getClass()));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, String.format("Error writing config for new item %s.", path), e);
        }
        return created;
    }

    static String getItemNameFromPath(String fullName) {
        int i = fullName.lastIndexOf('/');
        return i > -1 ? fullName.substring(i + 1) : fullName;
    }

    public static Set<String> getTemplates(Collection<GeneratedJob> jobs) {
        return Sets.newLinkedHashSet(Collections2.filter(Collections2.transform(jobs, new ExtractTemplate()), Predicates.notNull()));
    }

    public static class ExtractTemplate implements Function<GeneratedJob, String> {
        @Override
        public String apply(GeneratedJob input) {
            return input.getTemplateName();
        }
    }
}
