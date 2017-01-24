package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.helpers.ConfigFilesContext
import javaposse.jobdsl.plugin.structs.DescribableListContext

abstract class JenkinsJobParent extends JobParent {
    private static final String CONFIG_PROVIDER_TYPE = 'org.jenkinsci.lib.configprovider.ConfigProvider'

    Set referencedConfigs = new LinkedHashSet<>()

    @Override
    void configFiles(@DslContext(ConfigFilesContext) Closure closure) {
        jm.requirePlugin('config-file-provider', true)
        DescribableListContext context = new DescribableListContext(CONFIG_PROVIDER_TYPE, jm)
        ContextHelper.executeInContext(closure, context)
        referencedConfigs.addAll(context.values)
    }
}
