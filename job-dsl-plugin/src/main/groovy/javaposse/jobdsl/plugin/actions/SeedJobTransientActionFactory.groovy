package javaposse.jobdsl.plugin.actions

import hudson.Extension
import hudson.model.Action
import hudson.model.Item
import javaposse.jobdsl.plugin.DescriptorImpl
import javaposse.jobdsl.plugin.SeedReference
import jenkins.model.Jenkins
import jenkins.model.TransientActionFactory

import edu.umd.cs.findbugs.annotations.NonNull

@Extension
class SeedJobTransientActionFactory extends TransientActionFactory<Item> {
    @Override
    Class<Item> type() {
        Item
    }

    @NonNull
    @Override
    Collection<? extends Action> createFor(@NonNull Item target) {
        DescriptorImpl descriptor = Jenkins.get().getDescriptorByType(DescriptorImpl)
        SeedReference seedReference = descriptor.generatedJobMap[target.fullName]
        seedReference != null ? [new SeedJobAction(target, seedReference)] : []
    }
}
