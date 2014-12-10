package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import jenkins.model.Jenkins;

import java.util.Collection;
import java.util.Collections;

@Extension
public class SeedJobTransientProjectActionFactory extends TransientProjectActionFactory {

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        SeedReference seedReference = descriptor.getGeneratedJobMap().get(abstractProject.getFullName());
        if (seedReference != null) {
            return Collections.singletonList(
                    new SeedJobAction(seedReference.getSeedJobName(), seedReference.getTemplateJobName()));
        } else {
            return Collections.emptyList();
        }
    }
}
