package javaposse.jobdsl.plugin.fixtures

import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class ABean {
    @DataBoundSetter
    String prop

    @DataBoundConstructor
    ABean() {
    }
}
