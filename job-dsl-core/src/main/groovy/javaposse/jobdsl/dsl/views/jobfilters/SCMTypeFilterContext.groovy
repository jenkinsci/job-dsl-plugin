package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType

class SCMTypeFilterContext implements Context {

    String includeExcludeType = IncludeExcludeType.INCLUDE_MATCHED.value
    String scmType = SCMType.None.value
    

    void includeExcludeType(IncludeExcludeType type) {
        this.includeExcludeType = type.value
    }

    void scmType(SCMType scmType) {
        this.scmType = scmType.value
    }
    
    static enum SCMType {
        CVS('hudson.scm.CVSSCM'), CVSProjectSet('hudson.scm.CvsProjectset'), Git('hudson.plugins.git.GitSCM'), None('hudson.scm.NullSCM'), SVN('hudson.scm.SubversionSCM')

        final String value

        SCMType(String value) {
            this.value = value
        }
    }
}
