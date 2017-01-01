package javaposse.jobdsl.plugin

import hudson.model.Item
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import org.jenkinsci.plugins.scriptsecurity.scripts.UnapprovedUsageException
import org.jenkinsci.plugins.scriptsecurity.scripts.languages.GroovyLanguage
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval

class ScriptApprovalDslScriptLoader extends SecureDslScriptLoader {
    private final Item seedJob

    ScriptApprovalDslScriptLoader(JobManagement jobManagement, Item seedJob) {
        super(jobManagement)
        this.seedJob = seedJob
    }

    @Override
    protected GroovyCodeSource createGroovyCodeSource(ScriptRequest scriptRequest) {
        if (scriptRequest.body != null) {
            try {
                ScriptApproval.get().using(scriptRequest.body, GroovyLanguage.get())
            } catch (UnapprovedUsageException e) {
                throw new DslException(e.message, e)
            }
            super.createGroovyCodeSource(scriptRequest)
        } else {
            // do not allow scripts from location to avoid tampering with file content between check and execution
            throw new UnsupportedOperationException()
        }
    }

    protected Collection<ScriptRequest> createSecureScriptRequests(Collection<ScriptRequest> scriptRequests) {
        super.createSecureScriptRequests(scriptRequests).each {
            if (it.body) {
                ScriptApproval.get().configuring(
                        it.body,
                        GroovyLanguage.get(),
                        ApprovalContext.create().withItem(seedJob)
                )
            }
        }
    }
}
