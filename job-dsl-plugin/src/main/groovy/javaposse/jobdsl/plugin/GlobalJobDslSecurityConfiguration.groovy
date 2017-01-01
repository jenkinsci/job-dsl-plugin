package javaposse.jobdsl.plugin

import hudson.Extension
import jenkins.model.GlobalConfiguration
import jenkins.model.GlobalConfigurationCategory
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest

@Extension
class GlobalJobDslSecurityConfiguration extends GlobalConfiguration {
    final GlobalConfigurationCategory category = GlobalConfigurationCategory.get(GlobalConfigurationCategory.Security)

    boolean useScriptSecurity = true

    GlobalJobDslSecurityConfiguration() {
        load()
    }

    @Override
    boolean configure(StaplerRequest req, JSONObject json) {
        useScriptSecurity = json.has('useScriptSecurity')
        save()
        true
    }
}
