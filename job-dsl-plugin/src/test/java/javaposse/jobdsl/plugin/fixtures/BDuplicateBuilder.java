package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;

public class BDuplicateBuilder extends Builder {
    @DataBoundConstructor
    public BDuplicateBuilder(String foo) {
        this.foo = foo;
    }

    public final String getFoo() {
        return foo;
    }

    private final String foo;

    @Extension
    @Symbol("duplicate")
    public static class DescriptorImpl extends BuildStepDescriptor<Builder> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return false;
        }

        @Override
        public final String getDisplayName() {
            return "";
        }
    }
}
