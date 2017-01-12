package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.ContextType
import javaposse.jobdsl.dsl.ExtensibleContext

@ContextType('org.jenkinsci.lib.configprovider.model.Config')
class ConfigFilesContext implements ExtensibleContext {
}
