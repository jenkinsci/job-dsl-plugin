package javaposse.jobdsl.plugin

import hudson.model.Item
import hudson.security.ACL
import javaposse.jobdsl.dsl.DslException
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import jenkins.model.Jenkins
import org.acegisecurity.AccessDeniedException
import org.codehaus.groovy.control.CompilerConfiguration
import org.jenkinsci.plugins.scriptsecurity.sandbox.RejectedAccessException
import org.jenkinsci.plugins.scriptsecurity.sandbox.Whitelist
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.ClassLoaderWhitelist
import org.jenkinsci.plugins.scriptsecurity.sandbox.groovy.GroovySandbox
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.ProxyWhitelist
import org.jenkinsci.plugins.scriptsecurity.scripts.ApprovalContext
import org.jenkinsci.plugins.scriptsecurity.scripts.ScriptApproval

import java.util.concurrent.Callable

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
    protected ClassLoader prepareClassLoader(URL[] urlRoots, ClassLoader classLoader) {
        GroovySandbox.createSecureClassLoader(new WorkspaceClassLoader(urlRoots[0], classLoader, seedJob))
    }

    protected Collection<ScriptRequest> createSecureScriptRequests(Collection<ScriptRequest> scriptRequests) {
        scriptRequests.collect {
            // it is not safe to use additional classpath entries
            new ScriptRequest(it.body, it.urlRoots[0..0] as URL[], it.ignoreExisting, it.scriptPath, it.basePath)
        }
    }

    @Override
    protected JenkinsJobParent runScriptEngine(ScriptRequest scriptRequest, GroovyShell groovyShell) {
        if (ACL.SYSTEM == Jenkins.authentication) {
            // the build must run as an actual user
            throw new AccessDeniedException(Messages.SandboxDslScriptLoader_NotAuthenticated())
        }

        try {
            return super.runScriptEngine(scriptRequest, groovyShell)
        } catch (RejectedAccessException e) {
            ScriptApproval.get().accessRejected(e, ApprovalContext.create().withItem(seedJob))
            throw new DslException(e.message, e)
        }
    }

    @Override
    protected Script parseScript(GroovyShell groovyShell, GroovyCodeSource codeSource) {
        GroovySandbox.runInSandbox(new Callable<Script>() {
            @Override
            Script call() throws Exception {
                groovyShell.parse(codeSource)
            }
        }, createWhitelist())
    }

    @Override
    protected void runScript(Script script) {
        GroovySandbox.runInSandbox(new Callable<Object>() {
            @Override
            Object call() throws Exception {
                script.run()
            }
        }, new ProxyWhitelist(new ClassLoaderWhitelist(script.class.classLoader), createWhitelist()))
    }

    @Override
    protected boolean isGroovyShellCacheEnabled() {
        false
    }

    private static ProxyWhitelist createWhitelist() {
        new ProxyWhitelist(Whitelist.all(), new JobDslWhitelist())
    }

    private static class WorkspaceClassLoader extends URLClassLoader {
        private final Item seedJob

        WorkspaceClassLoader(URL workspaceUrl, ClassLoader parent, Item seedJob) {
            super([workspaceUrl] as URL[], parent)
            this.seedJob = seedJob
        }

        @Override
        URL findResource(String name) {
            if (!seedJob.hasPermission(Item.WORKSPACE)) {
                return null
            }

            super.findResource(name)
        }

        @Override
        Enumeration<URL> findResources(String name) throws IOException {
            if (!seedJob.hasPermission(Item.WORKSPACE)) {
                return Collections.emptyEnumeration()
            }

            super.findResources(name)
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            throw new ClassNotFoundException(name)
        }
    }
}
