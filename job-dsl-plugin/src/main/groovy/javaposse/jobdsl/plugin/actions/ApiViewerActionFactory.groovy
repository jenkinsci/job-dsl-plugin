package javaposse.jobdsl.plugin.actions

import hudson.Extension
import hudson.model.Action
import hudson.model.Project
import javaposse.jobdsl.plugin.ExecuteDslScripts
import jenkins.model.Jenkins
import jenkins.model.TransientActionFactory

import javax.annotation.Nonnull

@Extension
class ApiViewerActionFactory extends TransientActionFactory<Project> {
    @Override
    Class<Project> type() {
        Project
    }

    @Override
    Collection<? extends Action> createFor(@Nonnull Project target) {
        target.buildersList.contains(Jenkins.instance.getDescriptor(ExecuteDslScripts)) ? [new ApiViewerAction()] : []
    }
}
