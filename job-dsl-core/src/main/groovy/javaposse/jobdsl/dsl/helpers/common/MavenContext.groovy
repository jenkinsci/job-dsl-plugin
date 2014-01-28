package javaposse.jobdsl.dsl.helpers.common

import javaposse.jobdsl.dsl.helpers.Context

interface MavenContext extends Context {
    /**
     * Specifies the path to the root POM.
     * @param rootPOM path to the root POM
     */
    def rootPOM(String rootPOM)

    /**
     * Specifies the goals to execute.
     * @param goals the goals to execute
     */
    def goals(String goals)

    /**
     * Specifies the JVM options needed when launching Maven as an external process.
     * @param mavenOpts JVM options needed when launching Maven
     */
    def mavenOpts(String mavenOpts)

    /**
     * <localRepository class="hudson.maven.local_repo.PerJobLocalRepositoryLocator"/>
     *
     * Set to use isolated local Maven repositories.
     * @param location the local repository to use for isolation
     */
    def localRepository(LocalRepositoryLocation location)

    /**
     * Specifies the Maven installation for executing this step or job
     * @param name name of the Maven installation to use
     */
    def mavenInstallation(String name)

    public enum LocalRepositoryLocation {
        LocalToExecutor('hudson.maven.local_repo.PerExecutorLocalRepositoryLocator'),
        LocalToWorkspace('hudson.maven.local_repo.PerJobLocalRepositoryLocator')

        String type

        public LocalRepositoryLocation(String type) {
            this.type = type
        }
    }
}
