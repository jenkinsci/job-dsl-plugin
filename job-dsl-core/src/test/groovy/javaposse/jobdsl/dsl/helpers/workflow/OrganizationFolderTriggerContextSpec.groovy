package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification

class OrganizationFolderTriggerContextSpec extends Specification {

  private final JobManagement jobManagement = Mock(JobManagement)
  private final Item item = Mock(Item)
  private final OrganizationFolderTriggerContext context = new OrganizationFolderTriggerContext(jobManagement, item)

  def "no triggers configured by default"() {
    expect:
    context.triggerNodes.empty
  }

  def "can configure a PeriodicFolderTrigger"() {
    given:
    OrganizationFolderTriggerContext.PeriodicFolderTrigger expectedTrigger =
      OrganizationFolderTriggerContext.PeriodicFolderTrigger.EIGHT_HOURS

    when:
    context.periodicIfNotOtherwiseTriggered(expectedTrigger)

    then:
    context.triggerNodes.size() == 1
    with(context.triggerNodes[0]) {
      name() == 'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger'
      spec.size() == 1
      spec[0].value() == expectedTrigger.cron
      interval.size() == 1
      interval[0].value() == expectedTrigger.interval
    }
  }
}
