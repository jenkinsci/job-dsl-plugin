package javaposse.jobdsl.plugin.fixtures

import hudson.Extension
import hudson.model.AbstractDescribableImpl
import hudson.model.Descriptor
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class ADescribable extends AbstractDescribableImpl<ADescribable> {
    @DataBoundSetter
    String foo

    @DataBoundSetter
    int bar

    @DataBoundConstructor
    ADescribable() {
    }

    @Extension
    static class DescriptorImpl extends Descriptor<ADescribable> {
        final String displayName = null
    }
}
