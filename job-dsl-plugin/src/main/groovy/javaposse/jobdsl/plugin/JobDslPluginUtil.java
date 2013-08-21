package javaposse.jobdsl.plugin;

import hudson.model.Item;
import jenkins.model.Jenkins;
import jenkins.model.ModifiableTopLevelItemGroup;

public class JobDslPluginUtil {

    /**
     * Extract the ModifiableTopLevelItemGroup from the full name.
     */
    public static ModifiableTopLevelItemGroup getContextFromFullName(String fullName) {
        int i = fullName.lastIndexOf('/');
        Jenkins jenkins = Jenkins.getInstance();
        ModifiableTopLevelItemGroup ctx = jenkins;
        if (i > 0) {
            String contextName = fullName.substring(0, i);
            Item contextItem = jenkins.getItemByFullName(contextName);
            if (contextItem instanceof ModifiableTopLevelItemGroup) {
                ctx = (ModifiableTopLevelItemGroup) contextItem;
            }
        }
        return ctx;
    }

    /**
     * Extract the job name from the full name.
     */
    public static String getJobNameFromFullName(String fullName) {
        int i = fullName.lastIndexOf('/');
        return i > 0 ? fullName.substring(i+1) : fullName;
    }

}
