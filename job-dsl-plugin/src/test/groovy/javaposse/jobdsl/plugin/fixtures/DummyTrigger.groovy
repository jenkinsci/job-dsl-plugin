package javaposse.jobdsl.plugin.fixtures

import antlr.ANTLRException
import hudson.Extension
import hudson.model.Item
import hudson.model.Job
import hudson.triggers.Trigger
import hudson.triggers.TriggerDescriptor
import jenkins.mvn.SettingsProvider
import org.jenkinsci.Symbol
import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class DummyTrigger extends Trigger<Job> {
    @DataBoundSetter
    String aString

    @DataBoundSetter
    Integer anInteger

    @DataBoundSetter
    boolean aBoolean

    @DataBoundSetter
    Thread.State anEnum

    @DataBoundSetter
    SettingsProvider aHeterogeneous

    @DataBoundSetter
    List<SettingsProvider> aHeterogeneousList

    @DataBoundSetter
    ADescribable aHomogeneous

    @DataBoundSetter
    List<ADescribable> aHomogeneousList

    @DataBoundSetter
    ABean aHomogeneousBean

    @DataBoundSetter
    List<ABean> aHomogeneousBeanList

    @DataBoundSetter
    List<String> stringList

    @DataBoundSetter
    List<Thread.State> enumList

    @DataBoundConstructor
    DummyTrigger() throws ANTLRException {
    }

    @Extension
    @Symbol('dummy')
    static class DescriptorImpl extends TriggerDescriptor {
        final String displayName = null

        @Override
        boolean isApplicable(Item item) {
            false
        }
    }
}
