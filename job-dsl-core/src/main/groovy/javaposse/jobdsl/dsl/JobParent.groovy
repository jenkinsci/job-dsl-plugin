package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Sets

import java.util.logging.Level
import java.util.logging.Logger

abstract class JobParent extends Script implements DslFactory {
    private static final Logger LOGGER = Logger.getLogger(JobParent.name)

    JobManagement jm
    Set<Item> referencedJobs
    Set<View> referencedViews
    Set<ConfigFile> referencedConfigFiles
    List<String> queueToBuild

    protected JobParent() {
        referencedJobs = Sets.newLinkedHashSet()
        referencedViews = Sets.newLinkedHashSet()
        referencedConfigFiles = Sets.newLinkedHashSet()
        queueToBuild = Lists.newArrayList()
    }

    @Override
    Job job(Map<String, Object> arguments=[:], Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Job job = new Job(jm, arguments)

        // Configure with what we have already
        job.with(closure)

        // Save jobs, so that we know what to extract XML from
        referencedJobs << job

        // This job can have .configure { } called on
        job
    }

    @Override
    View view(Map<String, Object> arguments=[:], Closure closure) {
        ViewType viewType = arguments['type'] as ViewType ?: ViewType.ListView
        View view = viewType.viewClass.newInstance()
        view.with(closure)
        referencedViews << view

        // This view can have .configure { } called on
        view
    }

    @Override
    Folder folder(Closure closure) {
        Folder folder = new Folder()
        folder.with(closure)
        referencedJobs << folder
        folder
    }

    @Override
    ConfigFile configFile(Map<String, Object> arguments=[:], Closure closure) {
        ConfigFileType configFileType = arguments['type'] as ConfigFileType ?: ConfigFileType.Custom
        ConfigFile configFile = configFileType.configFileClass.newInstance(configFileType)
        configFile.with(closure)
        referencedConfigFiles << configFile

        configFile
    }

    @Override
    void queue(String jobName) {
        queueToBuild << jobName
    }

    @Override
    void queue(Job job) {
        Preconditions.checkArgument(job.name as Boolean)
        queueToBuild << job.name
    }

    @Override
    InputStream streamFileFromWorkspace(String filePath) {
        Preconditions.checkArgument(filePath as Boolean)
        jm.streamFileInWorkspace(filePath)
    }

    @Override
    String readFileFromWorkspace(String filePath) {
        Preconditions.checkArgument(filePath as Boolean)
        jm.readFileInWorkspace(filePath)
    }

    @Override
    String readFileFromWorkspace(String jobName, String filePath) {
        Preconditions.checkArgument(jobName as Boolean)
        Preconditions.checkArgument(filePath as Boolean)
        jm.readFileInWorkspace(jobName, filePath)
    }
}
