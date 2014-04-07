package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.helpers.AbstractContextHelper
import javaposse.jobdsl.dsl.helpers.Context

class PropertiesContext implements Context {
    List<Node> propertiesNodes = []

    /**
     * Add environment variables to the build.
     *
     * <project>
     *   <properties>
     *     <EnvInjectJobProperty>
     *       <info>
     *         <propertiesContent>TEST=foo BAR=123</propertiesContent>
     *         <loadFilesFromMaster>false</loadFilesFromMaster>
     *       </info>
     *       <on>true</on>
     *       <keepJenkinsSystemVariables>true</keepJenkinsSystemVariables>
     *       <keepBuildVariables>true</keepBuildVariables>
     *       <contributors/>
     *     </EnvInjectJobProperty>
     *   </properties>
     * <project>
     */
    def environmentVariables(Closure envClosure) {
        environmentVariables(null, envClosure)
    }

    def environmentVariables(Map<Object, Object> vars, Closure envClosure = null) {
        EnvironmentVariableContext envContext = new EnvironmentVariableContext()
        if (vars) {
            envContext.envs(vars)
        }
        AbstractContextHelper.executeInContext(envClosure, envContext)

        propertiesNodes << new NodeBuilder().'EnvInjectJobProperty' {
            envContext.addInfoToBuilder(delegate)
            on(true)
            keepJenkinsSystemVariables(envContext.keepSystemVariables)
            keepBuildVariables(envContext.keepBuildVariables)
            contributors()
        }
    }

    /**
     * <project>
     *     <properties>
     *         <hudson.plugins.throttleconcurrents.ThrottleJobProperty>
     *             <maxConcurrentPerNode>0</maxConcurrentPerNode>
     *             <maxConcurrentTotal>0</maxConcurrentTotal>
     *             <categories>
     *                 <string>CDH5-repo-update</string>
     *             </categories>
     *             <throttleEnabled>true</throttleEnabled>
     *             <throttleOption>category</throttleOption>
     *         </hudson.plugins.throttleconcurrents.ThrottleJobProperty>
     *     <properties>
     * </project>
     */
    def throttleConcurrentBuilds(Closure throttleClosure) {
        ThrottleConcurrentBuildsContext throttleContext = new ThrottleConcurrentBuildsContext()
        AbstractContextHelper.executeInContext(throttleClosure, throttleContext)

        propertiesNodes << new NodeBuilder().'hudson.plugins.throttleconcurrents.ThrottleJobProperty' {
            maxConcurrentPerNode throttleContext.maxConcurrentPerNode
            maxConcurrentTotal throttleContext.maxConcurrentTotal
            throttleEnabled throttleContext.throttleDisabled ? 'false' : 'true'
            if (throttleContext.categories.isEmpty()) {
                throttleOption 'project'
            } else {
                throttleOption 'category'
            }
            categories {
                throttleContext.categories.each { c ->
                    string c
                }
            }
        }
    }

    /**
     * Block build if certain jobs are running.
     *
     * <project>
     *     <properties>
     *         <hudson.plugins.buildblocker.BuildBlockerProperty>
     *             <useBuildBlocker>true</useBuildBlocker>  <!-- Always true -->
     *             <blockingJobs>JobA</blockingJobs>
     *         </hudson.plugins.buildblocker.BuildBlockerProperty>
     *     </properties>
     * </project>
     */
    def blockOn(Iterable<String> projectNames) {
        blockOn(projectNames.join('\n'))
    }

    /**
     * Block build if certain jobs are running.
     *
     * @param projectName Can be regular expressions. Newline delimited.
     */
    def blockOn(String projectName) {
        propertiesNodes << new NodeBuilder().'hudson.plugins.buildblocker.BuildBlockerProperty' {
            useBuildBlocker 'true'
            blockingJobs projectName
        }
    }

    /**
     * Priority of this job.
     * Requires the <a href="https://wiki.jenkins-ci.org/display/JENKINS/Priority+Sorter+Plugin">Priority Sorter Plugin</a>.
     * Default value is 100.
     *
     * <project>
     *     <properties>
     *         <hudson.queueSorter.PrioritySorterJobProperty plugin="PrioritySorter@1.3">
     *             <priority>100</priority>
     *         </hudson.queueSorter.PrioritySorterJobProperty>
     *     </properties>
     * </project>
     */
    def priority(int value) {
        propertiesNodes << new NodeBuilder().'hudson.queueSorter.PrioritySorterJobProperty' {
            delegate.priority value
        }
    }
}
