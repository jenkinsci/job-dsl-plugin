package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import groovy.util.Node;
import groovy.util.XmlParser;
import hudson.EnvVars;
import hudson.FilePath;
import hudson.Plugin;
import hudson.XmlFile;
import hudson.model.AbstractBuild;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Run;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.slaves.Cloud;
import hudson.util.VersionNumber;
import hudson.util.XStream2;
import javaposse.jobdsl.dsl.AbstractJobManagement;
import javaposse.jobdsl.dsl.ConfigFile;
import javaposse.jobdsl.dsl.ConfigFileType;
import javaposse.jobdsl.dsl.ConfigurationMissingException;
import javaposse.jobdsl.dsl.DslException;
import javaposse.jobdsl.dsl.GeneratedJob;
import javaposse.jobdsl.dsl.Job;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.NameNotProvidedException;
import javaposse.jobdsl.dsl.helpers.ExtensibleContext;
import javaposse.jobdsl.plugin.api.ContextExtensionPoint;
import javaposse.jobdsl.plugin.api.DslMethod;
import javaposse.jobdsl.plugin.api.DslSession;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;

import org.apache.commons.lang.ClassUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jenkinsci.lib.configprovider.ConfigProvider;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.vSphereCloud;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static hudson.model.Result.UNSTABLE;
import static hudson.model.View.createViewFromXML;
import static hudson.security.ACL.SYSTEM;
import static java.lang.String.format;
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.createNewConfig;
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.findConfig;
import static javaposse.jobdsl.plugin.ConfigFileProviderHelper.findConfigProvider;
import static javaposse.jobdsl.plugin.api.DslSession.clearCurrentSession;
import static javaposse.jobdsl.plugin.api.DslSession.setCurrentSession;
import static org.apache.commons.lang.reflect.MethodUtils.getMatchingAccessibleMethod;

/**
 * Manages Jenkins jobs, providing facilities to retrieve and create / update.
 */
public final class JenkinsJobManagement extends AbstractJobManagement {
    private static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());
    private static final XStream2 XSTREAM = new XStream2();

    private final EnvVars envVars;
    private final AbstractBuild<?, ?> build;
    private final LookupStrategy lookupStrategy;
    private final Map<String, DslSession> sessions = new HashMap<String, DslSession>();

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
    public String getConfig(String path) throws JobConfigurationNotFoundException {
        LOGGER.log(Level.INFO, format("Getting config for Job %s", path));
        String xml;

        if (path.isEmpty()) {
            throw new JobConfigurationNotFoundException(path);
        }

        try {
            xml = lookupJob(path);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, format("Named Job Config not found: %s", path));
            throw new JobConfigurationNotFoundException(path);
        }

        LOGGER.log(Level.FINE, format("Job config %s", xml));
        return xml;
    }

    @Override
    public boolean createOrUpdateConfig(String jobId, String path, String config, boolean ignoreExisting)
            throws NameNotProvidedException, ConfigurationMissingException {

        LOGGER.log(Level.INFO, format("createOrUpdateConfig for %s", path));
        boolean created = false;

        validateUpdateArgs(path, config);

        AbstractItem item = lookupStrategy.getItem(build.getProject(), path, AbstractItem.class);
        String jobName = getItemNameFromPath(path);
        Jenkins.checkGoodName(jobName);

        if (item == null) {
            created = createNewItem(jobId, path, config);
        } else if (!ignoreExisting) {
            created = updateExistingItem(jobId, item, config);
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
                        LOGGER.log(Level.WARNING, format("Could not create view within %s", parent.getClass()));
                    }
                } else if (!ignoreExisting) {
                    view.updateByXml(new StreamSource(inputStream));
                }
            } else if (parent == null) {
                throw new DslException(format(Messages.CreateView_UnknownParent(), path));
            } else {
                LOGGER.log(Level.WARNING, format("Could not create view within %s", parent.getClass()));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.WARNING, format("Error writing config for new view %s.", path), e);
        }
    }

    @Override
    public String createOrUpdateConfigFile(ConfigFile configFile, boolean ignoreExisting) {
        validateNameArg(configFile.getName());

        Jenkins jenkins = Jenkins.getInstance();

        if (jenkins.getPlugin("config-file-provider") == null) {
            throw new DslException(Messages.CreateOrUpdateConfigFile_PluginNotInstalled());
        }

        ConfigProvider configProvider = findConfigProvider(configFile.getType());
        if (configProvider == null) {
            throw new DslException(
                    format(Messages.CreateOrUpdateConfigFile_ConfigProviderNotFound(), configFile.getClass())
            );
        }

        Config config = findConfig(configProvider, configFile.getName());
        if (config == null) {
            config = configProvider.newConfig();
        } else if (ignoreExisting) {
            return config.id;
        }

        config = createNewConfig(config, configFile);
        if (config == null) {
            throw new DslException(
                    format(Messages.CreateOrUpdateConfigFile_UnknownConfigFileType(), configFile.getClass())
            );
        }

        configProvider.save(config);
        return config.id;
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
    public void queueJob(String path) throws NameNotProvidedException {
        validateNameArg(path);

        BuildableItem project = lookupStrategy.getItem(build.getParent(), path, BuildableItem.class);

        LOGGER.log(Level.INFO, format("Scheduling build of %s from %s", path, build.getParent().getName()));
        project.scheduleBuild(new Cause.UpstreamCause((Run) build));
    }


    @Override
    public InputStream streamFileInWorkspace(String relLocation) throws IOException {
        FilePath filePath = locateValidFileInWorkspace(build.getWorkspace(), relLocation);
        return filePath.read();
    }

    @Override
    public String readFileInWorkspace(String relLocation) throws IOException {
        FilePath filePath = locateValidFileInWorkspace(build.getWorkspace(), relLocation);
        return filePath.readToString();
    }

    @Override
    public String readFileInWorkspace(String jobName, String relLocation) throws IOException {
        Item item = Jenkins.getInstance().getItemByFullName(jobName);
        if (item instanceof AbstractProject) {
            FilePath workspace = ((AbstractProject) item).getSomeWorkspace();
            if (workspace != null) {
                try {
                    return locateValidFileInWorkspace(workspace, relLocation).readToString();
                } catch (IllegalStateException e) {
                    logWarning(Messages.ReadFileFromWorkspace_JobFileNotFound(), relLocation, jobName);
                }
            } else {
                logWarning(Messages.ReadFileFromWorkspace_WorkspaceNotFound(), relLocation, jobName);
            }
        } else {
            logWarning(Messages.ReadFileFromWorkspace_JobNotFound(), relLocation, jobName);
        }
        return null;
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

    @Override
    public VersionNumber getPluginVersion(String pluginShortName) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        return plugin == null ? null : plugin.getWrapper().getVersionNumber();
    }

    @Override
    public Integer getVSphereCloudHash(String name) {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins.getPlugin("vsphere-cloud") != null) {
            for (Cloud cloud : jenkins.clouds) {
                if (cloud instanceof vSphereCloud && ((vSphereCloud) cloud).getVsDescription().equals(name)) {
                    return ((vSphereCloud) cloud).getHash();
                }
            }
        }
        return null;
    }

    @Override
    public String getConfigFileId(ConfigFileType type, String name) {
        Jenkins jenkins = Jenkins.getInstance();
        if (jenkins.getPlugin("config-file-provider") != null) {
            ConfigProvider configProvider = findConfigProvider(type);
            if (configProvider != null) {
                Config config = findConfig(configProvider, name);
                if (config != null) {
                    return config.id;
                }
            }
        }
        return null;
    }

    public Node callExtension(String jobId, String name, Class<? extends ExtensibleContext> contextType, Object... args) {
        Map<ContextExtensionPoint, Method> candidates = findExtensionPoints(name, contextType, args);
        if (candidates.isEmpty()) {
            LOGGER.fine(
                    "Found no extension which provides method " + name + " with arguments " + Arrays.toString(args)
            );
            return null;
        } else if (candidates.size() > 1) {
            throw new ExtensionPointException(
                    "Found multiple extensions which provide method " + name + " with arguments " +
                            Arrays.toString(args) + ": " +
                            Arrays.toString(ClassUtils.toClass(candidates.keySet().toArray()))
            );
        }

        try {
            Map.Entry<ContextExtensionPoint, Method> candidate = candidates.entrySet().iterator().next();
            ContextExtensionPoint extensionPoint = candidate.getKey();
            Method method = candidate.getValue();
            
            setCurrentSession(getSession(jobId));
            Object result = method.invoke(extensionPoint, args);
            clearCurrentSession();
            
            String xml = XSTREAM.toXML(result);
            LOGGER.fine(
                    "Call to extension " + extensionPoint.getClass().getName() + "." + name + " with arguments " +
                            Arrays.toString(args) + " produced " + xml
            );
            return new XmlParser().parseText(xml);
        } catch (Exception e) {
            throw new ExtensionPointException("Error calling extension", e);
        }
    }

    private void markBuildAsUnstable(String message) {
        getOutputStream().println("Warning: " + message + " (" + getSourceDetails(getStackTrace()) + ")");
        build.setResult(UNSTABLE);
    }

    private FilePath locateValidFileInWorkspace(FilePath workspace, String relLocation) throws IOException {
        FilePath filePath = workspace.child(relLocation);
        try {
            if (!filePath.exists()) {
                throw new IllegalStateException(format("File %s does not exist in workspace", relLocation));
            }
        } catch (InterruptedException ie) {
            throw new IOException(ie);
        }
        return filePath;
    }

    private String lookupJob(String path) throws IOException {
        LOGGER.log(Level.FINE, format("Looking up item %s", path));

        AbstractItem item = lookupStrategy.getItem(build.getProject(), path, AbstractItem.class);
        if (item != null) {
            XmlFile xmlFile = item.getConfigFile();
            String jobXml = xmlFile.asString();
            LOGGER.log(Level.FINE, format("Looked up item with config %s", jobXml));
            return jobXml;
        } else {
            LOGGER.log(Level.WARNING, format("No item called %s could be found.", path));
            throw new IOException(format("No item called %s could be found.", path));
        }
    }

    private boolean updateExistingItem(String jobId, AbstractItem item, String config) {
        boolean created;

        // Leverage XMLUnit to perform diffs
        Diff diff;
        try {
            String oldJob = item.getConfigFile().asString();
            diff = XMLUnit.compareXML(oldJob, config);
            if (diff.similar()) {
                LOGGER.log(Level.FINE, format("Item %s is identical", item.getName()));
                return false;
            }
        } catch (Exception e) {
            // It's not a big deal if we can't diff, we'll just move on
            LOGGER.warning(e.getMessage());
        }

        LOGGER.log(Level.FINE, format("Updating item %s as %s", item.getName(), config));
        Source streamSource = new StreamSource(new StringReader(config));
        try {
            item.updateByXml(streamSource);

            setCurrentSession(getSession(jobId));
            for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
                extensionPoint.notifyItemUpdated(item);
            }
            clearCurrentSession();

            created = true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, format("Error writing updated item to file."), e);
            created = false;
        }
        return created;
    }

    private boolean createNewItem(String jobId, String path, String config) {
        LOGGER.log(Level.FINE, format("Creating item as %s", config));
        boolean created = false;

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(build.getProject(), path);
            String itemName = getItemNameFromPath(path);
            if (parent instanceof ModifiableTopLevelItemGroup) {
                Item item = ((ModifiableTopLevelItemGroup) parent).createProjectFromXML(itemName, is);

                setCurrentSession(getSession(jobId));
                for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
                    extensionPoint.notifyItemCreated(item);
                }
                clearCurrentSession();

                created = true;
            } else if (parent == null) {
                throw new DslException(format(Messages.CreateItem_UnknownParent(), path));
            } else {
                LOGGER.log(Level.WARNING, format("Could not create item within %s", parent.getClass()));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.log(Level.WARNING, "Unsupported encoding used in config. Should be UTF-8.");
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, format("Error writing config for new item %s.", path), e);
        }
        return created;
    }

    static String getItemNameFromPath(String path) {
        int i = path.lastIndexOf('/');
        return i > -1 ? path.substring(i + 1) : path;
    }

    private DslSession getSession(String jobId) {
        DslSession session = sessions.get(jobId);
        if (session == null) {
            session = new DslSession();
            sessions.put(jobId, session);
        }
        return session;
    }

    public static Set<String> getTemplates(Collection<GeneratedJob> jobs) {
        return Sets.newLinkedHashSet(Collections2.filter(Collections2.transform(jobs, new ExtractTemplate()), Predicates.notNull()));
    }

    private static Map<ContextExtensionPoint, Method> findExtensionPoints(String name,
                                                                          Class<? extends ExtensibleContext> contextType,
                                                                          Object... args) {
        Class[] parameterTypes = ClassUtils.toClass(args);
        Map<ContextExtensionPoint, Method> candidates = new HashMap<ContextExtensionPoint, Method>();

        // Find extensions that match any @DslMethod annotated method with the given name and parameters
        for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
            Method candidateMethod = getMatchingAccessibleMethod(extensionPoint.getClass(), name, parameterTypes);
            if (candidateMethod != null) {
                DslMethod annotation = candidateMethod.getAnnotation(DslMethod.class);
                if (annotation != null && annotation.context().isAssignableFrom(contextType)) {
                    candidates.put(extensionPoint, candidateMethod);
                }
            }
        }

        return candidates;
    }

    public static class ExtractTemplate implements Function<GeneratedJob, String> {
        @Override
        public String apply(GeneratedJob input) {
            return input.getTemplateName();
        }
    }
}
