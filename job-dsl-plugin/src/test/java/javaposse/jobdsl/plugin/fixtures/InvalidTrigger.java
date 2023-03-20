package javaposse.jobdsl.plugin.fixtures;

import hudson.model.Job;
import hudson.triggers.Trigger;
import org.kohsuke.stapler.DataBoundConstructor;

public class InvalidTrigger extends Trigger<Job> {
    @DataBoundConstructor
    public InvalidTrigger() {}
}
