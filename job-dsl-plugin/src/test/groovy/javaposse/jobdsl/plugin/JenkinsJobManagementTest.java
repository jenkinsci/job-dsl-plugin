package javaposse.jobdsl.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import static org.junit.Assert.assertNull;

public class JenkinsJobManagementTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();
    
    @Test
    public void getCredentialsIdWithoutCredentialsPlugin() {
        // setup
        JenkinsJobManagement jobManagement = new JenkinsJobManagement();
        
        // when
        String id = jobManagement.getCredentialsId("test");
        
        // then
        assertNull(id);
    }
}
