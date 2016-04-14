package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.AbstractProject
import hudson.tasks.BuildStepDescriptor
import hudson.tasks.Builder
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor

class BDuplicateBuilder extends Builder {
    final String foo

    @DataBoundConstructor
    BDuplicateBuilder(String foo) {
        this.foo = foo
    }

    @Extension
    @Symbol('duplicate')
    static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        final String displayName = null

        @Override
        boolean isApplicable(Class<? extends AbstractProject> aClass) {
            false
        }
    }
}
