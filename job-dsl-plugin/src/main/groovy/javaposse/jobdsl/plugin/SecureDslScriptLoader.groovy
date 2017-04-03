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

    protected Collection<ScriptRequest> createSecureScriptRequests(Collection<ScriptRequest> scriptRequests) {
        scriptRequests.collect {
            // it is not safe to use additional classpath entries
            new ScriptRequest(it.location, it.body, new URL[0], it.ignoreExisting, it.scriptPath)
        }
    }
}
