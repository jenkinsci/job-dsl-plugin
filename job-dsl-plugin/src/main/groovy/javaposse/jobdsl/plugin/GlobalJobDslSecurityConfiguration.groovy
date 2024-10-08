package javaposse.jobdsl.plugin

import hudson.Extension
import jenkins.model.GlobalConfiguration
import jenkins.model.GlobalConfigurationCategory
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest2

@Extension
class GlobalJobDslSecurityConfiguration extends GlobalConfiguration {
    GlobalConfigurationCategory getCategory() {
        GlobalConfigurationCategory.get(GlobalConfigurationCategory.Security)
    }

    boolean useScriptSecurity = true

    GlobalJobDslSecurityConfiguration() {
        load()
    }

    @Override
    boolean configure(StaplerRequest2 req, JSONObject json) {
        useScriptSecurity = json.has('useScriptSecurity')
        save()
        true
    }
}
