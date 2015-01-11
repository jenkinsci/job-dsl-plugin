package javaposse.jobdsl.plugin

import org.apache.commons.io.FileUtils
import org.apache.tools.ant.filters.StringInputStream
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule

class JobConfigGeneratorTest {

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    private final JobConfigGenerator generator = new JobConfigGenerator('test-job')

    @Test
    void testCreateConfigFromXML() {
        InputStream xml = new StringInputStream(promo)
        generator.createConfigFromXML(xml, 'promotions/test-promotion')
        File config = configFile()
        assert config.exists()
        assert FileUtils.readFileToString(config) == promo
    }

    private File configFile() {
        File root = jenkinsRule.instance.rootDir
        File jobs = new File(root, 'jobs')
        File testjob = new File(jobs, 'test-job')
        File promos = new File(testjob, 'promotions')
        File p1 = new File(promos, 'test-promotion')
        new File(p1, 'config.xml')
    }

    private final String promo = '''<?xml version='1.0' encoding='UTF-8'?>
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
