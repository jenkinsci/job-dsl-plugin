package javaposse.jobdsl.plugin

import hudson.model.Item
import hudson.security.ACL
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import jenkins.model.Jenkins
import org.acegisecurity.AccessDeniedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.GroovySandbox
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval

class SandboxDslScriptLoader extends SecureDslScriptLoader {
    private final Item seedJob

    SandboxDslScriptLoader(JobManagement jobManagement, Item seedJob) {
        super(jobManagement)
        this.seedJob = seedJob
    }

    @Override
    protected CompilerConfiguration createCompilerConfiguration() {
        GroovySandbox.createSecureCompilerConfiguration()
    }

    @Override
    protected ClassLoader prepareClassLoader(ClassLoader classLoader) {
        GroovySandbox.createSecureClassLoader(classLoader)
    }

    @Override
    protected void runScript(Script script) {
        if (ACL.SYSTEM == Jenkins.authentication) {
            // the build must run as an actual user
            throw new AccessDeniedException(Messages.SandboxDslScriptLoader_NotAuthenticated())
        }

        try {
            GroovySandbox.run(script, new ProxyWhitelist(Whitelist.all(), new JobDslWhitelist()))
        } catch (RejectedAccessException e) {
            ScriptApproval.get().accessRejected(e, ApprovalContext.create().withItem(seedJob))
            throw new DslException(e.message, e)
        }
    }
}
