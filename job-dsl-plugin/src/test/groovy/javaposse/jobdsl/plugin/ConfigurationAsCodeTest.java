package javaposse.jobdsl.plugin;

import io.jenkins.plugins.casc.ConfigurationAsCode;
import io.jenkins.plugins.casc.yaml.YamlSource;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNotNull;

/**
 * @author <a href="mailto:nicolas.deloof@gmail.com">Nicolas De Loof</a>
 */
public class ConfigurationAsCodeTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void configure_seed_job() throws Exception {
        ConfigurationAsCode.get().configureWith(new YamlSource(getClass().getResourceAsStream("ConfigurationAsCodeTest.yaml"), YamlSource.READ_FROM_INPUTSTREAM));
        assertNotNull(j.jenkins.getItem("testJob1"));
        assertNotNull(j.jenkins.getItem("testJob2"));
    }
}
