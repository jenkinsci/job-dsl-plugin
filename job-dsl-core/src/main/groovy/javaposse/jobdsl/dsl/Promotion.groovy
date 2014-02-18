package javaposse.jobdsl.dsl

class Promotion extends AdditionalXmlConfig {

    public Promotion(String name) {
        super(XmlConfigType.PROMOTION)
        this.name = name
    }

    protected String getRelativePath() {
        return "promotions/" + name
    }

    protected Node getNode() {
        Node project = new XmlParser().parse(new StringReader(emptyPromotionTemplate))
        return project
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
