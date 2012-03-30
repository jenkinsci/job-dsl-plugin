package javaposse.jobdsl;

import java.util.logging.Logger;
import java.util.logging.Level;

import groovy.lang.Closure;
import groovy.lang.Script;

public abstract class JobParent extends Script {
    private static final Logger LOGGER = Logger.getLogger(JobParent.class.getName());

    public Job job(Closure closure) {
        LOGGER.log(Level.FINE, "Got closure and have ${secretJobManagement}")
        Job job = new Job(secretJobManagement)
        closure.delegate = job
        closure.call()
        return job
    }
}