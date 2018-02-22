package javaposse.jobdsl.plugin;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.thoughtworks.xstream.io.xml.XppDriver;
import groovy.util.Node;
import groovy.util.XmlParser;
import hudson.FilePath;
import hudson.Plugin;
import hudson.XmlFile;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Items;
import hudson.model.Job;
import hudson.model.ModifiableViewGroup;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewGroup;
import hudson.slaves.Cloud;
import hudson.util.VersionNumber;
import javaposse.jobdsl.dsl.AbstractJobManagement;
import javaposse.jobdsl.dsl.DslException;
import javaposse.jobdsl.dsl.DslScriptException;
import javaposse.jobdsl.dsl.ExtensibleContext;
import javaposse.jobdsl.dsl.JobConfigurationNotFoundException;
import javaposse.jobdsl.dsl.NameNotProvidedException;
import javaposse.jobdsl.dsl.UserContent;
import javaposse.jobdsl.plugin.ExtensionPointHelper.DslExtension;
import jenkins.model.DirectlyModifiableTopLevelItemGroup;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;
import org.apache.commons.io.FilenameUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.jenkinsci.plugins.vSphereCloud;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringReader;
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
import static java.lang.String.format;

/**
 * Manages Jenkins jobs, providing facilities to retrieve and create / update.
 */
public class JenkinsJobManagement extends AbstractJobManagement {
    private static final Logger LOGGER = Logger.getLogger(JenkinsJobManagement.class.getName());

    private final Map<String, ?> envVars;
    private final Run<?, ?> run;
    private final FilePath workspace;
    private final Item project;
    private final LookupStrategy lookupStrategy;
    private final Map<javaposse.jobdsl.dsl.Item, DslEnvironment> environments = new HashMap<>();
    private boolean failOnMissingPlugin;
    private boolean unstableOnDeprecation;

    public JenkinsJobManagement(PrintStream outputLogger, Map<String, ?> envVars, Run<?, ?> run,
                                FilePath workspace, LookupStrategy lookupStrategy) {
        super(outputLogger);
        this.envVars = envVars;
        this.run = run;
        this.workspace = workspace;
        this.project = run == null ? null : run.getParent();
        this.lookupStrategy = lookupStrategy;
    }

    public JenkinsJobManagement(PrintStream outputLogger, Map<String, ?> envVars, File workspace) {
        this(outputLogger, envVars, null, new FilePath(workspace.getAbsoluteFile()), LookupStrategy.JENKINS_ROOT);
    }

    @SuppressWarnings("WeakerAccess") // JENKINS-45921
    public void setFailOnMissingPlugin(boolean failOnMissingPlugin) {
        this.failOnMissingPlugin = failOnMissingPlugin;
    }

    void setUnstableOnDeprecation(boolean unstableOnDeprecation) {
        this.unstableOnDeprecation = unstableOnDeprecation;
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

        LOGGER.log(Level.INFO, format("createOrUpdateConfig for %s", path));

        validateNameArg(path);

        AbstractItem item = lookupStrategy.getItem(project, path, AbstractItem.class);
        String jobName = FilenameUtils.getName(path);
        Jenkins.checkGoodName(jobName);

        if (item == null) {
            createNewItem(path, dslItem);
            return true;
        } else if (!ignoreExisting) {
            return updateExistingItem(item, dslItem);
        }
        return false;
    }

    @Override
    public void createOrUpdateView(String path, String config, boolean ignoreExisting, boolean deleteExisting) {
        validateUpdateArgs(path, config);
        String viewBaseName = FilenameUtils.getName(path);
        Jenkins.checkGoodName(viewBaseName);
        try {
            InputStream inputStream = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(project, path);
            if (parent instanceof ViewGroup) {
                ViewGroup parentViewGroup = (ViewGroup) parent;
                View view = parentViewGroup.getView(viewBaseName);
                if (deleteExisting && view != null) {
                    parentViewGroup.deleteView(view);
                    view = null;
                }
                if (view == null) {
                    if (parent instanceof ModifiableViewGroup) {
                        ((ModifiableViewGroup) parent).checkPermission(View.CREATE);
                        ((ModifiableViewGroup) parent).addView(createViewFromXML(viewBaseName, inputStream));
                    } else {
                        throw new DslException(format(Messages.CreateView_UnsupportedParent(), parent.getFullName(), parent.getClass()));
                    }
                } else if (!ignoreExisting) {
                    checkItemType(view, inputStream);
                    inputStream.reset();
                    view.updateByXml(new StreamSource(inputStream));
                }
            } else if (parent == null) {
                throw new DslException(format(Messages.CreateView_UnknownParent(), path));
            } else {
                throw new DslException(format(Messages.CreateView_UnsupportedParent(), parent.getFullName(), parent.getClass()));
            }
        } catch (IOException e) {
            throw new DslException(e);
        }
    }

    @Override
    public void createOrUpdateUserContent(UserContent userContent, boolean ignoreExisting) {
        // As in git-userContent-plugin:
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        try {
            FilePath file = Jenkins.getInstance().getRootPath().child("userContent").child(userContent.getPath());
            if (!(file.exists() && ignoreExisting)) {
                file.getParent().mkdirs();
                file.copyFrom(userContent.getContent());
            }
        } catch (Exception e) {
            throw new DslException(
                    format(Messages.CreateOrUpdateUserContent_Exception(), userContent.getPath(), e.getMessage())
            );
        }
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> result = new HashMap<>(envVars);
        if (project != null && !result.containsKey("SEED_JOB")) {
            result.put("SEED_JOB", project);
        }
        return result;
    }

    @Override
    public void queueJob(String path) throws NameNotProvidedException {
        validateNameArg(path);

        BuildableItem project = lookupStrategy.getItem(this.project, path, BuildableItem.class);
        project.checkPermission(Item.BUILD);

        LOGGER.log(Level.INFO, format("Scheduling build of %s from %s", path, project.getName()));
        project.scheduleBuild(run == null ? new JobDslCause() : new Cause.UpstreamCause(run));
    }


    @Override
    public InputStream streamFileInWorkspace(String relLocation) throws IOException, InterruptedException {
        if (project != null) {
            project.checkPermission(Item.WORKSPACE);
        }
        FilePath filePath = locateValidFileInWorkspace(workspace, relLocation);
        return filePath.read();
    }

    @Override
    public String readFileInWorkspace(String relLocation) throws IOException, InterruptedException {
        if (project != null) {
            project.checkPermission(Item.WORKSPACE);
        }
        FilePath filePath = locateValidFileInWorkspace(workspace, relLocation);
        return filePath.readToString();
    }

    @Override
    public String readFileInWorkspace(String jobName, String relLocation) throws IOException, InterruptedException {
        Item item = Jenkins.getInstance().getItemByFullName(jobName);
        if (item instanceof AbstractProject) {
            item.checkPermission(Item.WORKSPACE);
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
    protected void logDeprecationWarning(String subject, String details) {
        super.logDeprecationWarning(subject, details);
        if (unstableOnDeprecation && run != null) {
            run.setResult(UNSTABLE);
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
            failOrMarkBuildAsUnstable(
                    "plugin '" + pluginShortName + "' needs to be installed",
                    failIfMissing || failOnMissingPlugin
            );
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
                    failIfMissing || failOnMissingPlugin
            );
        } else if (plugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber(version))) {
            failOrMarkBuildAsUnstable(
                    "plugin '" + pluginShortName + "' needs to be updated to version " + version + " or later",
                    failIfMissing || failOnMissingPlugin
            );
        }
    }

    @Override
    public void requireMinimumCoreVersion(String version) {
        if (!isMinimumCoreVersion(version)) {
            failOrMarkBuildAsUnstable("Jenkins needs to be updated to version " + version + " or later", false);
        }
    }

    @Override
    public boolean isMinimumPluginVersionInstalled(String pluginShortName, String version) {
        Plugin plugin = Jenkins.getInstance().getPlugin(pluginShortName);
        return plugin != null && !plugin.getWrapper().getVersionNumber().isOlderThan(new VersionNumber(version));
    }

    @Override
    public boolean isMinimumCoreVersion(String version) {
        return !Jenkins.getVersion().isOlderThan(new VersionNumber(version));
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
    public void renameJobMatching(final String previousNames, String destination) throws IOException {
        final ItemGroup context = lookupStrategy.getContext(project);
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
        Set<DslExtension> candidates = ExtensionPointHelper.findExtensionPoints(name, contextType, args);
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
            DslExtension extension = Iterables.getOnlyElement(candidates);
            if (extension.isDeprecated()) {
                logDeprecationWarning(name);
            }
            Object result = extension.call(getSession(item), this, args);
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
            if (run != null) {
                run.setResult(UNSTABLE);
            }
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

        AbstractItem item = lookupStrategy.getItem(project, path, AbstractItem.class);
        if (item != null) {
            item.checkPermission(Item.EXTENDED_READ);
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

        item.checkPermission(Item.EXTENDED_READ);

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
        } catch (IOException e) {
            throw new DslException(e);
        }
        return true;
    }

    private void checkItemType(AbstractItem item, javaposse.jobdsl.dsl.Item dslItem) {
        Node oldConfig;

        item.checkPermission(Item.EXTENDED_READ);
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

    private void checkItemType(View view, InputStream config) {
        Class viewType = Jenkins.XSTREAM2.getMapper().realClass(new XppDriver().createReader(config).getNodeName());
        if (!viewType.equals(view.getClass())) {
            throw new DslException(format(Messages.UpdateExistingView_ViewTypeDoesNotMatch(), view.getViewName()));
        }
    }

    private void createNewItem(String path, javaposse.jobdsl.dsl.Item dslItem) {
        String config = dslItem.getXml();
        LOGGER.log(Level.FINE, format("Creating item as %s", config));

        try {
            InputStream is = new ByteArrayInputStream(config.getBytes("UTF-8"));

            ItemGroup parent = lookupStrategy.getParent(project, path);
            String itemName = FilenameUtils.getName(path);
            if (parent instanceof ModifiableTopLevelItemGroup) {
                Item project = ((ModifiableTopLevelItemGroup) parent).createProjectFromXML(itemName, is);
                notifyItemCreated(project, dslItem);
            } else if (parent == null) {
                throw new DslException(format(Messages.CreateItem_UnknownParent(), path));
            } else {
                throw new DslException(format(Messages.CreateItem_UnsupportedParent(), parent.getFullName(), parent.getClass()));
            }
        } catch (IOException e) {
            throw new DslException(e);
        }
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
        if (item == null) {
            return null;
        }
        DslEnvironment session = environments.get(item);
        if (session == null) {
            session = new DslEnvironmentImpl(this, item);
            environments.put(item, session);
        }
        return session;
    }

    private void renameJob(Job from, String to) throws IOException {
        LOGGER.info(format("Renaming job %s to %s", from.getFullName(), to));

        ItemGroup fromParent = from.getParent();
        ItemGroup toParent = lookupStrategy.getParent(project, to);
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

    private static class JobDslCause extends Cause {
        @Override
        public String getShortDescription() {
            return "Started by a Job DSL script";
        }
    }
}
