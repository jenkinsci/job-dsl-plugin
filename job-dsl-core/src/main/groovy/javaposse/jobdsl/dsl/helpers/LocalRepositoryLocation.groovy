package javaposse.jobdsl.dsl.helpers

/**
 * @since 1.31
 */
enum LocalRepositoryLocation {
    LOCAL_TO_EXECUTOR('hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'),
    LOCAL_TO_WORKSPACE('hudson.maven.local_repo.PerJobLocalRepositoryLocator')

    final String type

    LocalRepositoryLocation(String type) {
        this.type = type
    }
}
