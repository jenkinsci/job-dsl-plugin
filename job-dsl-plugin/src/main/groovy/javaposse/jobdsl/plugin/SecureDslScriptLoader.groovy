package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.GeneratedItems
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import org.jenkinsci.plugins.scriptsecurity.scripts.ClasspathEntry
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval
import org.jenkinsci.plugins.scriptsecurity.scripts.UnapprovedClasspathException

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
            // first root always points to workspace directory -> remove that because directories are considered unsafe
            URL[] secureUrlRoots = it.urlRoots.length > 1 ? it.urlRoots[1..-1] : []
            secureUrlRoots.each { URL url ->
                try {
                    ScriptApproval.get().using(new ClasspathEntry(url.toString()))
                } catch (UnapprovedClasspathException e) {
                    throw new DslException(e.message, e)
                }
            }

            new ScriptRequest(it.location, it.body, secureUrlRoots, it.ignoreExisting, it.scriptPath)
        }
    }
}
