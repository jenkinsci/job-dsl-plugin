package javaposse.jobdsl.dsl;

import java.util.logging.Logger
import java.util.logging.Level

import com.google.common.collect.Sets;

import groovy.lang.Closure
import groovy.lang.Script

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.class.getName());

    JobManagement jm;
    Set<Job> referencedJobs

    public JobParent() {
        referencedJobs = Sets.newHashSet()
    }

    public Job job(Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${secretJobManagement}")
        Job job = new Job(secretJobManagement)
        closure.delegate = job
        closure.call()

        // In lieu of AST transformations, we queue up the blocks then execute
        job.execute()

        // TODO check name field

        referencedJobs.add(job)

        return job
    }
}