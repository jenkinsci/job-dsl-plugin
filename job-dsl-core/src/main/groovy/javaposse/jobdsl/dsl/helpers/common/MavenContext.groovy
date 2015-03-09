package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.Context

@Deprecated
interface MavenContext extends Context {
    @Deprecated
    enum LocalRepositoryLocation {
        LocalToExecutor(javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation.LOCAL_TO_EXECUTOR),
        LocalToWorkspace(javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation.LOCAL_TO_WORKSPACE)

        final javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation location

        LocalRepositoryLocation(javaposse.jobdsl.dsl.helpers.LocalRepositoryLocation location) {
            this.location = location
        }
    }
}
