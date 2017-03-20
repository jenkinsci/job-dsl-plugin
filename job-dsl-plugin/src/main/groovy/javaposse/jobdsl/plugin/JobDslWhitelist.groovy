package javaposse.jobdsl.plugin

import hudson.Extension
import hudson.model.Item
import hudson.model.ItemGroup
import hudson.security.ACL
import hudson.security.AccessControlled
import hudson.security.Permission
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.JobParent
import javaposse.jobdsl.dsl.ViewFactory
import jenkins.model.Jenkins
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AbstractWhitelist

import java.lang.reflect.Method

/**
 * Allows methods defined in {@link Context}.
 * The exception is top-level methods until the right permission checks have been made.
 * @see org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AclAwareWhitelist
 */
// TODO Jenkins 2: @CompileStatic
@Extension
class JobDslWhitelist extends AbstractWhitelist {
    @Override
    boolean permitsMethod(Method method, Object receiver, Object[] args) {
        Class<?> declaringClass = method.getDeclaringClass();
        if (!Context.isAssignableFrom(declaringClass)) {
            return false;
        } else if (declaringClass == ViewFactory) {
            return false; // TODO check View.CREATE/CONFIGURE
        } else if (declaringClass == DslFactory) {
            Class<?> returnType = method.getReturnType();
            if (javaposse.jobdsl.dsl.Item.isAssignableFrom(returnType)) {
                // TODO need some sort of abstraction defined for internal use to access getItem/getParent cleanly
                JobManagement jm = ((JobParent) receiver).jm;
                if (jm instanceof InterruptibleJobManagement) {
                    jm = ((InterruptibleJobManagement) jm).delegate;
                }
                JenkinsJobManagement jjm = (JenkinsJobManagement) jm;
                Item existing = jjm.lookupStrategy.getItem(jjm.project, (String) args[0], Item.class);
                if (existing != null) {
                    existing.checkPermission(Item.CONFIGURE);
                } else {
                    ItemGroup parent = jjm.lookupStrategy.getParent(jjm.project, (String) args[0]);
                    if (parent instanceof AccessControlled) {
                        ((AccessControlled) parent).checkPermission(Item.CREATE);
                    } else {
                        // Not sure what we got; safest to restrict to admins.
                        Jenkins.getActiveInstance().checkPermission(Jenkins.ADMINISTER);
                    }
                }
                return authenticated();
            } else if (ConfigFile.isAssignableFrom(returnType)) {
                Jenkins.getActiveInstance().checkPermission(Jenkins.ADMINISTER);
                return authenticated();
            } else if (method.getName().equals("queue")) {
                return false; // TODO need to look up item, check Item.BUILD
            } else {
                return authenticated(); // need to do per-method access control checks in JenkinsJobManagement
            }
        } else if (declaringClass == Job && method.getName().equals("using")) {
            return false; // TODO look up existing job and check Item.VIEW_CONFIGURATION?
        } else { // internal DSL method which on its own does nothing
            return true;
        }
    }

    /** Whether this build is running as an actual user. */
    private static boolean authenticated() {
        return !ACL.SYSTEM.equals(Jenkins.getAuthentication());
    }

}
