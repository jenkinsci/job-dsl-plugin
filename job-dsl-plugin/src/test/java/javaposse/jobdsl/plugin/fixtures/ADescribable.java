package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ADescribable extends AbstractDescribableImpl<ADescribable> {
    @DataBoundConstructor
    public ADescribable() {}

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public int getBar() {
        return bar;
    }

    public void setBar(int bar) {
        this.bar = bar;
    }

    @DataBoundSetter
    private String foo;

    @DataBoundSetter
    private int bar;

    @Extension
    public static class DescriptorImpl extends Descriptor<ADescribable> {
        @Override
        public final String getDisplayName() {
            return "";
        }
    }
}
