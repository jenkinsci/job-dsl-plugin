package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.collect.Sets

import java.util.logging.Level
import java.util.logging.Logger

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.getName());

    JobManagement jm;
    Set<JobItem> referencedJobs
    List<String> queueToBuild

    public JobParent() {
        referencedJobs = Sets.newLinkedHashSet()
        queueToBuild = Lists.newArrayList()
    }

    public Job job(Map<String, Object> arguments=[:], Closure closure) {
        return job(jm, referencedJobs, arguments, closure, null)
    }

    public Folder folder(Closure closure) {
        return folder(jm, referencedJobs, closure, null)
    }

    public static Job job(JobManagement jm, Set<JobItem> referencedJobs, Map<String, Object> arguments, Closure closure, JobItem parent) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Job job = new Job(jm, arguments, parent)

        // Configure with what we have already
        job.with(closure)

        // Save jobs, so that we know what to extract XML from
        referencedJobs << job

        // This job can have .configure { } called on
        return job
    }

    public static Folder folder(JobManagement jm, Set<JobItem> referencedJobs, Closure closure, JobItem parent) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Folder folder = new Folder(jm, referencedJobs, parent)

        // Configure with what we have already
        folder.with(closure)

        // Save folders, so that we know what to extract XML from
        referencedJobs << folder

        // This folder can have .configure { } called on
        return folder
    }

    /**
     * Schedule a job to be run later. Validation of the job name isn't done until after the DSL has run.
     * @param jobName
     * @return
     */
    public queue(String jobName) {
        queueToBuild << jobName
    }

    /**
     * Schedule a job to be run later.
     * @param jobName
     * @return
     */
    public queue(Job job) {
        // TODO Consider lazily evaluating in case some Closure sets the name
        Preconditions.checkArgument(job.name as Boolean)
        queueToBuild << job.name
    }

    public InputStream streamFileFromWorkspace(String filePath) throws IOException {
        Preconditions.checkArgument(filePath as Boolean)
        return jm.streamFileInWorkspace(filePath);
    }

    public String readFileFromWorkspace(String filePath) throws IOException {
        Preconditions.checkArgument(filePath as Boolean)
        return jm.readFileInWorkspace(filePath);
    }
}
