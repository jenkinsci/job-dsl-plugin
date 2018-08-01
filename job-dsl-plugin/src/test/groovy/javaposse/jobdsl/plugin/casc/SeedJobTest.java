package javaposse.jobdsl.plugin.casc;

import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.casc.ConfigurationAsCode;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class SeedJobTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void configure_seed_job() throws Exception {
        final Jenkins jenkins = Jenkins.getInstance();
        ConfigurationAsCode.get().configure(getClass().getResource("SeedJobTest.yml").toExternalForm());
        final TopLevelItem test = jenkins.getItem("configuration-as-code");
        assertNotNull(test);
        assertTrue(test instanceof WorkflowMultiBranchProject);
    }
}
