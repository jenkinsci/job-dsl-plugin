package javaposse.jobdsl.plugin.fixtures.other

import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class ADuplicateBean {
    @DataBoundSetter
    String prop

    @DataBoundConstructor
    ADuplicateBean() {
    }
}
