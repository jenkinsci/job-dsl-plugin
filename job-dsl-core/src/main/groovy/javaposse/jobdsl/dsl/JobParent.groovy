package javaposse.jobdsl.dsl;

import java.util.logging.Logger
import java.util.logging.Level

import com.google.common.collect.Sets;

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.getName());

    // job types
    static String maven = 'maven'

    JobManagement jm;
    Set<Job> referencedJobs

    public JobParent() {
        referencedJobs = Sets.newHashSet()
    }

    public Job job(Map<String, Object> arguments=[:], Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${jm}")
        Job job = new Job(jm, arguments)

        // Configure with what we have already
        closure.delegate = job
        closure.call()

        // Save jobs, so that we know what to extract XML from
        referencedJobs.add(job)

        // This job can have .configure { } called on
        return job
    }
}