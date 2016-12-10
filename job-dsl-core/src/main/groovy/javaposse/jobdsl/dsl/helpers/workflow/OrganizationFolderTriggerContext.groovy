package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.helpers.triggers.ItemTriggerContext

/**
 * @since 1.56
 */
class OrganizationFolderTriggerContext extends ItemTriggerContext {

  OrganizationFolderTriggerContext(JobManagement jobManagement, Item item) {
    super(jobManagement, item)
  }

  /**
   * Sets the periodic trigger for when this has not been triggered.
   * @since 1.56
   */
  void periodicIfNotOtherwiseTriggered(PeriodicFolderTrigger periodicFolderTrigger) {
    triggerNodes << new NodeBuilder().'com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger' {
      spec(periodicFolderTrigger.cron)
      interval(periodicFolderTrigger.interval)
    }
  }

  /**
   * The drop down options for a periodic trigger if not triggered by something.
   * @since 1.56
   */
  enum PeriodicFolderTrigger {
    ONE_MINUTE('* * * * *', 60000L),
    TWO_MINUTES('* * * * *', 120000L),
    FIVE_MINUTES('*/12 * * * *', 300000L),
    TEN_MINUTES('*/6 * * * *', 600000L),
    FIFTEEN_MINUTES('*/6 * * * *', 900000L),
    TWENTY_MINUTES('*/6 * * * *', 1200000L),
    TWENTY_FIVE_MINUTES('*/6 * * * *', 1500000L),
    THIRTY_MINUTES('*/2 * * * *', 1800000L),
    ONE_HOUR('H * * * *', 3600000L),
    TWO_HOURS('H * * * *', 7200000L),
    FOUR_HOURS('H * * * *', 14400000L),
    EIGHT_HOURS('H H * * *', 28800000L),
    TWELVE_HOURS('H H * * *', 43200000L),
    ONE_DAY('H H * * *', 86400000L),
    TWO_DAYS('H H * * *', 172800000L),
    ONE_WEEK('H H * * *', 604800000L),
    TWO_WEEKS('H H * * *', 1209600000L),
    FOUR_WEEKS('H H * * *', 2419200000L),

    final String cron
    final long interval

    private PeriodicFolderTrigger(String cron, long interval) {
      this.cron = cron
      this.interval = interval
    }
  }
}
