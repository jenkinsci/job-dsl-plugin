package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.DslScriptException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.RequiresPlugin

class SystemGroovyCommandContext extends SystemGroovyContext {
    private final JobManagement jobManagement

    boolean sandbox

    SystemGroovyCommandContext(JobManagement jobManagement) {
        this.jobManagement = jobManagement
    }

    /**
     * If set, run the Groovy script in a sandbox with limited abilities. Defaults to {@code false}.
     *
     * @since 1.67
     */
    @RequiresPlugin(id = 'groovy', minimumVersion = '2.0')
    void sandbox(boolean sandbox = true) {
        this.sandbox = sandbox
    }

    @Override
    void classpath(String classpath) {
        if (jobManagement.isMinimumPluginVersionInstalled('groovy', '2.0')) {
            try {
                new URL(classpath)
            } catch (MalformedURLException e) {
                throw new DslScriptException("classpath must be a valid URL: ${e.message}")
            }
        }
        super.classpath(classpath)
    }
}
