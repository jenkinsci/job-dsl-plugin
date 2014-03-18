package javaposse.jobdsl.dsl.views.jobfilters

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType

class JobTypeFilterContext implements Context {

    String includeExcludeType = IncludeExcludeType.INCLUDE_MATCHED.value
    String jobType = JobType.FreeStyle.value
    

    void includeExcludeType(IncludeExcludeType type) {
        this.includeExcludeType = type.value
    }

    void jobType(JobType jobType) {
        this.jobType = jobType.value
    }
    
    static enum JobType {
        FreeStyle('hudson.model.FreeStyleProject'), Maven('hudson.maven.MavenModuleSet'), Matrix('hudson.matrix.MatrixProject'), External('hudson.model.ExternalJob')

        final String value

        JobType(String value) {
            this.value = value
        }
    }
}
