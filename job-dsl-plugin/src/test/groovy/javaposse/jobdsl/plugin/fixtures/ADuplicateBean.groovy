package javaposse.jobdsl.plugin.fixtures

import org.kohsuke.stapler.DataBoundConstructor
import org.kohsuke.stapler.DataBoundSetter

class ADuplicateBean {
    @DataBoundSetter
    String prop

    @SuppressWarnings('UnnecessaryConstructor')
    @DataBoundConstructor
    ADuplicateBean() {
    }
}
