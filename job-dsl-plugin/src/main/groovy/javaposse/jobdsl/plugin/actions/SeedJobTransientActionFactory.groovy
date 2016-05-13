package javaposse.jobdsl.plugin.actions

import hudson.Extension
import hudson.model.Action
import hudson.model.Item
import javaposse.jobdsl.plugin.DescriptorImpl
import javaposse.jobdsl.plugin.SeedReference
import jenkins.model.Jenkins
import jenkins.model.TransientActionFactory

import javax.annotation.Nonnull

@Extension
class SeedJobTransientActionFactory extends TransientActionFactory<Item> {
    @Override
    Class<Item> type() {
        Item
    }

    @Nonnull
    @Override
    Collection<? extends Action> createFor(@Nonnull Item target) {
        DescriptorImpl descriptor = Jenkins.instance.getDescriptorByType(DescriptorImpl)
        SeedReference seedReference = descriptor.generatedJobMap[target.fullName]
        seedReference != null ? [new SeedJobAction(target, seedReference)] : []
    }
}
