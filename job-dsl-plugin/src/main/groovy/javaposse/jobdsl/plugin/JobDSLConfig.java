package javaposse.jobdsl.plugin;

import hudson.Extension;

import hudson.model.Descriptor;
import hudson.util.FormValidation;

import javaposse.jobdsl.dsl.helpers.step.SystemGroovyContext;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import jenkins.model.GlobalConfiguration;

@Extension
public class JobDSLConfig extends GlobalConfiguration {

    private static String noExitSecurityManager;

    public JobDSLConfig() {
        // load configfile
        load();
    }

    @Override
    public synchronized void load() {
        super.load();
        initSecurityManager();
    }

    @Override
    public synchronized void save() {
        super.save();
        initSecurityManager();
    }

    public void initSecurityManager() {
        // create Configfile if not exists
        if (noExitSecurityManager == null) {
            noExitSecurityManager = "false"; //default
            save();
        } else {

            if (noExitSecurityManager.toUpperCase().equals("TRUE")) {
                System.setSecurityManager(new org.codehaus.groovy.tools.shell.util.NoExitSecurityManager());
            } else {
                if ((System.getSecurityManager()
                        instanceof org.codehaus.groovy.tools.shell.util.NoExitSecurityManager)) {
                    System.setSecurityManager(null);
                }
            }
        }
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json)
            throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }

    public String getNoExitSecurityManager() {
        return noExitSecurityManager;
    }

    public void setNoExitSecurityManager(String noExitSecurityManager) {
        this.noExitSecurityManager = noExitSecurityManager;
        initSecurityManager();
    }

    public FormValidation doCheckNoExitSecurityManager(
            @QueryParameter String noExitSecurityManager) {
        if (noExitSecurityManager.toUpperCase().equals("TRUE")) {
            if (!(System.getSecurityManager()
                    instanceof org.codehaus.groovy.tools.shell.util.NoExitSecurityManager)) {
                return FormValidation.warning(System.getSecurityManager() +
                        " is actually set as SecurityManager for the Jenkins JVM. This will be overwritten on save!");
            }
        }
        return FormValidation.ok();
    }
}
