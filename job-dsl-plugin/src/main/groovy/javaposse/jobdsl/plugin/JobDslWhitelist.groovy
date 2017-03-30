package javaposse.jobdsl.plugin

import hudson.model.Item
import hudson.model.ItemGroup
import hudson.model.View
import hudson.model.ViewGroup
import hudson.security.AccessControlled
import javaposse.jobdsl.dsl.ConfigFile
import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ViewFactory
import jenkins.model.Jenkins
import org.apache.commons.io.FilenameUtils
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AbstractWhitelist

import java.lang.reflect.Method

/**
 * Allows methods defined in {@link Context}.
 * The exception is top-level methods until the right permission checks have been made.
 * @see org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.AclAwareWhitelist
 */
class JobDslWhitelist extends AbstractWhitelist {
    private final JenkinsJobManagement jobManagement

    JobDslWhitelist(JobManagement jobManagement) {
        if (jobManagement instanceof InterruptibleJobManagement) {
            this.jobManagement = ((InterruptibleJobManagement) jobManagement).delegate
        } else if (jobManagement instanceof JenkinsJobManagement) {
            this.jobManagement = (JenkinsJobManagement) jobManagement
        } else {
            throw new IllegalArgumentException("jobManagement must be an instance of ${JenkinsJobManagement.name}")
        }
    }

    @Override
    boolean permitsMethod(Method method, Object receiver, Object[] args) {
        Class<?> declaringClass = method.declaringClass
        if (!Context.isAssignableFrom(declaringClass)) {
            return false
        } else if (declaringClass == ViewFactory) {
            ItemGroup parent = jobManagement.lookupStrategy.getParent(jobManagement.project, (String) args[0])
            if (parent instanceof ViewGroup) {
                View view = ((ViewGroup) parent).getView(FilenameUtils.getName((String) args[0]))
                if (view == null) {
                    ((ViewGroup) parent).checkPermission(View.CREATE)
                } else {
                    view.checkPermission(View.CONFIGURE)
                }
            } else {
                // Not sure what we got; safest to restrict to admins.
                Jenkins.activeInstance.checkPermission(Jenkins.ADMINISTER)
            }
            return true
        } else if (declaringClass == DslFactory) {
            Class<?> returnType = method.returnType
            if (javaposse.jobdsl.dsl.Item.isAssignableFrom(returnType)) {
                Item existing = jobManagement.lookupStrategy.getItem(jobManagement.project, (String) args[0], Item)
                if (existing != null) {
                    existing.checkPermission(Item.CONFIGURE)
                } else {
                    ItemGroup parent = jobManagement.lookupStrategy.getParent(jobManagement.project, (String) args[0])
                    if (parent instanceof AccessControlled) {
                        ((AccessControlled) parent).checkPermission(Item.CREATE)
                    } else {
                        // Not sure what we got; safest to restrict to admins.
                        Jenkins.activeInstance.checkPermission(Jenkins.ADMINISTER)
                    }
                }
                return true
            } else if (ConfigFile.isAssignableFrom(returnType)) {
                Jenkins.activeInstance.checkPermission(Jenkins.ADMINISTER)
                return true
            } else {
                return true // need to do per-method access control checks in JenkinsJobManagement
            }
        } else { // internal DSL method which on its own does nothing
            return true
        }
    }
}
