package javaposse.jobdsl.plugin.fixtures.other;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class ADuplicateBean {
    @DataBoundConstructor
    public ADuplicateBean() {}

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    @DataBoundSetter
    private String prop;
}
