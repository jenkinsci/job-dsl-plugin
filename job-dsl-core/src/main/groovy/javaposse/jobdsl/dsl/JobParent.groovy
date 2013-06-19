package javaposse.jobdsl.dsl

import com.google.common.base.Preconditions
import com.google.common.collect.Lists;

import java.util.logging.Logger
import java.util.logging.Level

import com.google.common.collect.Sets;

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.getName());

    JobManagement jm;
    Set<Job> referencedJobs
    List<String> queueToBuild

    public JobParent() {
        referencedJobs = Sets.newHashSet()
        queueToBuild = Lists.newArrayList()
    }

    public Job job(Map<String, Object> arguments=[:], Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Job job = new Job(jm, arguments)

        // Configure with what we have already
        job.with(closure)

        // Save jobs, so that we know what to extract XML from
        referencedJobs << job

        // This job can have .configure { } called on
        return job
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