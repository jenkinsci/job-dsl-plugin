package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.Folder;
import com.cloudbees.plugins.credentials.CredentialsProvider;
import com.cloudbees.plugins.credentials.common.StandardCredentials;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
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
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.slaves.Cloud;
import hudson.util.VersionNumber;
import javaposse.jobdsl.dsl.AbstractJobManagement;
import javaposse.jobdsl.dsl.ConfigFile;
import javaposse.jobdsl.dsl.ConfigFileType;
import javaposse.jobdsl.dsl.DslException;
import javaposse.jobdsl.dsl.DslScriptException;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.NameNotProvidedException;
import javaposse.jobdsl.dsl.UserContent;
import javaposse.jobdsl.dsl.helpers.ExtensibleContext;
import javaposse.jobdsl.plugin.ExtensionPointHelper.ExtensionPointMethod;
import jenkins.model.DirectlyModifiableTopLevelItemGroup;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;
import org.apache.commons.io.FilenameUtils;
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
import java.lang.reflect.InvocationTargetException;
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

/**
 * Manages Jenkins jobs, providing facilities to retrieve and create / update.
 */
public final class JenkinsJobManagement extends AbstractJobManagement {
    private static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    private final EnvVars envVars;
    private final AbstractBuild<?, ?> build;
    private final LookupStrategy lookupStrategy;
    private final Map<javaposse.jobdsl.dsl.Item, DslEnvironment> environments =
            new HashMap<javaposse.jobdsl.dsl.Item, DslEnvironment>();

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
    public boolean createOrUpdateConfig(javaposse.jobdsl.dsl.Item dslItem, boolean ignoreExisting)
            throws NameNotProvidedException {
        String path = dslItem.getName();
        String config = dslItem.getXml();

        LOGGER.log(Level.INFO, format("createOrUpdateConfig for %s", path));
        boolean created = false;

        validateUpdateArgs(path, config);

        AbstractItem item = lookupStrategy.getItem(build.getProject(), path, AbstractItem.class);
        String jobName = FilenameUtils.getName(path);
        Jenkins.checkGoodName(jobName);

        if (item == null) {
            created = createNewItem(path, dslItem);
        } else if (!ignoreExisting) {
            created = updateExistingItem(item, dslItem);
        }
        return created;
    }

    @Override
    public void createOrUpdateView(String path, String config, boolean ignoreExisting) {
        validateUpdateArgs(path, config);
        String viewBaseName = FilenameUtils.getName(path);
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
    public void createOrUpdateUserContent(UserContent userContent, boolean ignoreExisting) {
        try {
            FilePath file = Jenkins.getInstance().getRootPath().child("userContent").child(userContent.getPath());
            if (!(file.exists() && ignoreExisting)) {
                file.getParent().mkdirs();
                file.copyFrom(userContent.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DslException(
                    format(Messages.CreateOrUpdateUserContent_Exception(), userContent.getPath(), e.getMessage())
            );
        }
    }

    @Override
    public Map<String, String> getParameters() {
        return envVars;
    }

    @Override
    @Deprecated
    public String getCredentialsId(String credentialsDescription) {
        Jenkins jenkins = Jenkins.getInstance();
        Plugin credentialsPlugin = jenkins.getPlugin("credentials");
        if (credentialsPlugin != null && !credentialsPlugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber("1.6"))) {
            for (CredentialsProvider credentialsProvider : jenkins.getExtensionList(CredentialsProvider.class)) {
                for (StandardCredentials credentials : credentialsProvider.getCredentials(StandardCredentials.class, jenkins, SYSTEM)) {
                    if (credentials.getId().equals(credentialsDescription)) {
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
    public InputStream streamFileInWorkspace(String relLocation) throws IOException, InterruptedException {
        FilePath filePath = locateValidFileInWorkspace(build.getWorkspace(), relLocation);
        return filePath.read();
    }

    @Override
    public String readFileInWorkspace(String relLocation) throws IOException, InterruptedException {
        FilePath filePath = locateValidFileInWorkspace(build.getWorkspace(), relLocation);
        return filePath.readToString();
    }

    @Override
    public String readFileInWorkspace(String jobName, String relLocation) throws IOException, InterruptedException {
        Item item = Jenkins.getInstance().getItemByFullName(jobName);
        if (item instanceof AbstractProject) {
            FilePath workspace = ((AbstractProject) item).getSomeWorkspace();
            if (workspace != null) {
                try {
                    return locateValidFileInWorkspace(workspace, relLocation).readToString();
                } catch (DslScriptException e) {
                    logWarning(format(Messages.ReadFileFromWorkspace_JobFileNotFound(), relLocation, jobName));
                }
            } else {
                logWarning(format(Messages.ReadFileFromWorkspace_WorkspaceNotFound(), relLocation, jobName));
            }
        } else {
            logWarning(format(Messages.ReadFileFromWorkspace_JobNotFound(), relLocation, jobName));
        }
        return null;
    }

    @Override
    public void logPluginDeprecationWarning(String pluginShortName, String minimumVersion) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        if (plugin != null && plugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber(minimumVersion))) {
            logDeprecationWarning(
                    "support for " + plugin.getWrapper().getDisplayName() + " versions older than " + minimumVersion
            );
        }
    }

    @Override
    public void requirePlugin(String pluginShortName) {
        requirePlugin(pluginShortName, false);
    }

    @Override
    public void requirePlugin(String pluginShortName, boolean failIfMissing) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        if (plugin == null) {
            failOrMarkBuildAsUnstable("plugin '" + pluginShortName + "' needs to be installed", failIfMissing);
        }
    }

    @Override
    public void requireMinimumPluginVersion(String pluginShortName, String version) {
        requireMinimumPluginVersion(pluginShortName, version, false);
    }

    @Override
    public void requireMinimumPluginVersion(String pluginShortName, String version, boolean failIfMissing) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        if (plugin == null) {
            failOrMarkBuildAsUnstable(
                    "version " + version + " or later of plugin '" + pluginShortName + "' needs to be installed",
                    failIfMissing
            );
        } else if (plugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber(version))) {
            failOrMarkBuildAsUnstable(
                    "plugin '" + pluginShortName + "' needs to be updated to version " + version + " or later",
                    failIfMissing
            );
        }
    }

    @Override
    public void requireMinimumCoreVersion(String version) {
        if (Jenkins.getVersion().isOlderThan(new VersionNumber(version))) {
            failOrMarkBuildAsUnstable("Jenkins needs to be updated to version " + version + " or later", false);
        }
    }

    @Override
    public VersionNumber getPluginVersion(String pluginShortName) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        return plugin == null ? null : plugin.getWrapper().getVersionNumber();
    }

    @Override
    public VersionNumber getJenkinsVersion() {
        return Jenkins.getVersion();
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

    @Override
    public void renameJobMatching(final String previousNames, String destination) throws IOException {
        final ItemGroup context = lookupStrategy.getContext(build.getProject());
        Collection<Job> items = Jenkins.getInstance().getAllItems(Job.class);
        Collection<Job> matchingJobs = Collections2.filter(items, new Predicate<Job>() {
            @Override
            public boolean apply(Job topLevelItem) {
                return topLevelItem.getRelativeNameFrom(context).matches(previousNames);
            }
        });
        if (matchingJobs.size() == 1) {
            renameJob(matchingJobs.iterator().next(), destination);
        } else if (matchingJobs.size() > 1) {
            throw new DslException(format(Messages.RenameJobMatching_MultipleJobsFound(), matchingJobs));
        }
    }

    @Override
    public Set<String> getPermissions(String descriptorId) {
        return PermissionsHelper.getPermissions(descriptorId);
    }

    @Override
    public Node callExtension(String name, javaposse.jobdsl.dsl.Item item,
                              Class<? extends ExtensibleContext> contextType, Object... args) throws Throwable {
        Set<ExtensionPointMethod> candidates = ExtensionPointHelper.findExtensionPoints(name, contextType, args);
        if (candidates.isEmpty()) {
            LOGGER.fine(
                    "Found no extension which provides method " + name + " with arguments " + Arrays.toString(args)
            );
            return null;
        } else if (candidates.size() > 1) {
            throw new DslException(format(
                    Messages.CallExtension_MultipleCandidates(),
                    name,
                    Arrays.toString(args),
                    Arrays.toString(candidates.toArray())
            ));
        }

        try {
            Object result = Iterables.getOnlyElement(candidates).call(getSession(item), args);
            return result == null ? NO_VALUE : new XmlParser().parseText(Items.XSTREAM2.toXML(result));
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    private void failOrMarkBuildAsUnstable(String message, boolean fail) {
        if (fail) {
            throw new DslScriptException(message);
        } else {
            logWarning(message);
            build.setResult(UNSTABLE);
        }
    }

    private FilePath locateValidFileInWorkspace(FilePath workspace, String relLocation) throws IOException, InterruptedException {
        FilePath filePath = workspace.child(relLocation);
        if (!filePath.exists()) {
            throw new DslScriptException(format("File %s does not exist in workspace", relLocation));
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

    private boolean updateExistingItem(AbstractItem item, javaposse.jobdsl.dsl.Item dslItem) {
        String config = dslItem.getXml();
        boolean created;

        // Leverage XMLUnit to perform diffs
        Diff diff;
        try {
            String oldJob = item.getConfigFile().asString();
            diff = XMLUnit.compareXML(oldJob, config);
            if (diff.identical()) {
                LOGGER.log(Level.FINE, format("Item %s is identical", item.getName()));
                notifyItemUpdated(item, dslItem);
                return false;
            }
        } catch (Exception e) {
            // It's not a big deal if we can't diff, we'll just move on
            LOGGER.warning(e.getMessage());
        }

        checkItemType(item, dslItem);

        LOGGER.log(Level.FINE, format("Updating item %s as %s", item.getName(), config));
        Source streamSource = new StreamSource(new StringReader(config));
        try {
            item.updateByXml(streamSource);
            notifyItemUpdated(item, dslItem);
            created = true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error writing updated item to file.", e);
            created = false;
        }
        return created;
    }

    private void checkItemType(AbstractItem item, javaposse.jobdsl.dsl.Item dslItem) {
        Node oldConfig;

        try {
            oldConfig = new XmlParser().parse(item.getConfigFile().getFile());
        } catch (Exception e) {
            throw new DslException(format(
                    Messages.UpdateExistingItem_CouldNotReadConfig(),
                    item.getConfigFile().getFile().getAbsolutePath(),
                    item.getFullName()
            ), e);
        }

        if (!oldConfig.name().equals(dslItem.getNode().name())) {
            throw new DslException(format(
                    Messages.UpdateExistingItem_ItemTypeDoesNotMatch(),
                    item.getFullName()
            ));
        }
    }

    private boolean createNewItem(String path, javaposse.jobdsl.dsl.Item dslItem) {
        String config = dslItem.getXml();
        LOGGER.log(Level.FINE, format("Creating item as %s", config));
        boolean created = false;

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(build.getProject(), path);
            String itemName = FilenameUtils.getName(path);
            if (parent instanceof ModifiableTopLevelItemGroup) {
                Item project = ((ModifiableTopLevelItemGroup) parent).createProjectFromXML(itemName, is);
                notifyItemCreated(project, dslItem);
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

    private void notifyItemCreated(Item item, javaposse.jobdsl.dsl.Item dslItem) {
        DslEnvironment session = getSession(dslItem);
        for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
            extensionPoint.notifyItemCreated(item, session);
        }
    }

    private void notifyItemUpdated(Item item, javaposse.jobdsl.dsl.Item dslItem) {
        DslEnvironment session = getSession(dslItem);
        for (ContextExtensionPoint extensionPoint : ContextExtensionPoint.all()) {
            extensionPoint.notifyItemUpdated(item, session);
        }
    }

    private DslEnvironment getSession(javaposse.jobdsl.dsl.Item item) {
        DslEnvironment session = environments.get(item);
        if (session == null) {
            session = new DslEnvironmentImpl();
            environments.put(item, session);
        }
        return session;
    }

    private void renameJob(Job from, String to) throws IOException {
        LOGGER.info(format("Renaming job %s to %s", from.getFullName(), to));

        ItemGroup fromParent = from.getParent();
        ItemGroup toParent = lookupStrategy.getParent(build.getProject(), to);
        if (toParent == null) {
            throw new DslException(format(Messages.RenameJobMatching_UnknownParent(), from.getFullName(), to));
        }
        if (fromParent != toParent) {
            LOGGER.info(format("Moving Job %s to folder %s", fromParent.getFullName(), toParent.getFullName()));
            if (toParent instanceof DirectlyModifiableTopLevelItemGroup) {
                DirectlyModifiableTopLevelItemGroup itemGroup = (DirectlyModifiableTopLevelItemGroup) toParent;
                move(from, itemGroup);
            } else {
                throw new DslException(format(
                        Messages.RenameJobMatching_DestinationNotFolder(),
                        from.getFullName(),
                        toParent.getFullName()
                ));
            }
        }
        from.renameTo(FilenameUtils.getName(to));
    }

    @SuppressWarnings("unchecked")
    private static <I extends AbstractItem & TopLevelItem> I move(Item item, DirectlyModifiableTopLevelItemGroup destination) throws IOException {
        return Items.move((I) item, destination);
    }
}
