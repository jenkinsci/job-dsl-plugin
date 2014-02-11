package javaposse.jobdsl.plugin

import javax.xml.transform.stream.StreamSource

import org.apache.commons.io.FileUtils
import org.apache.tools.ant.filters.StringInputStream
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule

class PromotionsGeneratorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    private PromotionsGenerator generator = new PromotionsGenerator("test-promotion", "test-job");

    @Test
    public void testCreatePromotionFromXML() throws Exception {
        InputStream xml = new StringInputStream(promo)
        generator.createPromotionFromXML(xml)
        File config = getConfigFile()
        assert config.exists()
        assert FileUtils.readFileToString(config) == promo
    }

    private File getConfigFile() {
        File root = jenkinsRule.getInstance().getRootDir()
        File jobs = new File(root, "jobs")
        File testjob = new File(jobs, "test-job")
        File promos = new File(testjob, "promotions")
        File p1 = new File(promos, "test-promotion")
        File config = new File(p1, "config.xml")
        return config
    }

    private String promo = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.promoted__builds.PromotionProcess plugin='promoted-builds@2.15'>
  <actions/>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class='hudson.scm.NullSCM'/>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <conditions>
    <hudson.plugins.promoted__builds.conditions.ManualCondition>
      <users>name</users>
      <parameterDefinitions/>
    </hudson.plugins.promoted__builds.conditions.ManualCondition>
  </conditions>
  <icon>star-green</icon>
  <buildSteps/>
</hudson.plugins.promoted__builds.PromotionProcess>'''
}
