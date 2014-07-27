package javaposse.jobdsl.dsl.additional

class Promotion extends AdditionalXmlConfig {

    Promotion(String name) {
        this.name = name
    }

    String getRelativePath() {
        'promotions/' + name
    }

    protected Node getRootNode() {
        new XmlParser().parse(new StringReader(emptyPromotionTemplate))
    }

    def emptyPromotionTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.plugins.promoted__builds.PromotionProcess plugin="promoted-builds@2.15">
  <actions/>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <scm class="hudson.scm.NullSCM"/>
  <canRoam>false</canRoam>
  <disabled>false</disabled>
  <blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>
  <blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>
  <triggers/>
  <concurrentBuild>false</concurrentBuild>
  <conditions/>
  <icon/>
  <buildSteps/>
</hudson.plugins.promoted__builds.PromotionProcess>
'''
}
