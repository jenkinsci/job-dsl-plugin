package javaposse.jobdsl.plugin;

import hudson.model.ListView;
import hudson.model.View;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class JenkinsJobManagementTest {
    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule();

    JenkinsJobManagement jobManagement = new JenkinsJobManagement();

    @Test
    public void getCredentialsIdWithoutCredentialsPlugin() {
        // when
        String id = jobManagement.getCredentialsId("test");
        
        // then
        assertNull(id);
    }

    @Test
    public void createView() {
        // when
        jobManagement.createOrUpdateView("test-view", "<hudson.model.ListView/>", false);

        // then
        View view = jenkinsRule.getInstance().getView("test-view");
        assertNotNull(view);
        assertTrue(view instanceof ListView);
    }

    @Test
    public void updateView() throws IOException {
        // setup
        jenkinsRule.getInstance().addView(new ListView("test-view"));

        // when
        jobManagement.createOrUpdateView("test-view", "<hudson.model.ListView><description>lorem ipsum</description></hudson.model.ListView>", false);

        // then
        View view = jenkinsRule.getInstance().getView("test-view");
        assertNotNull(view);
        assertTrue(view instanceof ListView);
        assertEquals("lorem ipsum", view.getDescription());
    }

    @Test
    public void updateViewIgnoreChanges() throws IOException {
        // setup
        jenkinsRule.getInstance().addView(new ListView("test-view"));

        // when
        jobManagement.createOrUpdateView("test-view", "<hudson.model.ListView><description>lorem ipsum</description></hudson.model.ListView>", true);

        // then
        View view = jenkinsRule.getInstance().getView("test-view");
        assertNotNull(view);
        assertTrue(view instanceof ListView);
        assertNull(view.getDescription());
    }

    @Test
    public void createViewInvalidConfig() {
        // when
        jobManagement.createOrUpdateView("test-view", "<hudson.model.ListView>", false);

        // then
        View view = jenkinsRule.getInstance().getView("test-view");
        assertNull(view);
    }
}
