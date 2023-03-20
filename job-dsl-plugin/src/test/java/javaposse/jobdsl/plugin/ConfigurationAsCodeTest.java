package javaposse.jobdsl.plugin;

import io.jenkins.plugins.casc.misc.ConfiguredWithCode;
import io.jenkins.plugins.casc.misc.JenkinsConfiguredWithCodeRule;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ConfigurationAsCodeTest {

    @ClassRule
    @ConfiguredWithCode("ConfigurationAsCodeTest.yaml")
    public static JenkinsConfiguredWithCodeRule j = new JenkinsConfiguredWithCodeRule();

    @Test
    public void configure_seed_job() {
        assertNotNull(j.jenkins.getItem("testJob1"));
        assertNotNull(j.jenkins.getItem("testJob2"));
        assertNotNull(j.jenkins.getItem("testJob3"));
    }
}
