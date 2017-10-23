package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.GeneratedItems
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ScriptRequest

abstract class SecureDslScriptLoader extends JenkinsDslScriptLoader {
    protected SecureDslScriptLoader(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    GeneratedItems runScripts(Collection<ScriptRequest> scriptRequests) throws IOException {
        super.runScripts(createSecureScriptRequests(scriptRequests))
    }

    @Override
    protected ClassLoader prepareClassLoader(URL[] urlRoots, ClassLoader classLoader) {
        new URLClassLoader([] as URL[], classLoader)
    }

    protected abstract Collection<ScriptRequest> createSecureScriptRequests(Collection<ScriptRequest> scriptRequests)
}
