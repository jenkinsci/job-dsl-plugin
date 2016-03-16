package javaposse.jobdsl.dsl.views.jobfilter

enum BuildStatusType {
    COMPLETED('Completed'),
    STARTED('Started'),
    STABLE('Stable'),
    UNSTABLE('Unstable'),
    FAILED('Failed'),
    NOT_STABLE('NotStable'),
    TRIGGERED_BY_SCM_POLL('TriggeredByScmPoll'),
    TRIGGERED_BY_TIMER('TriggeredByTimer'),
    TRIGGERED_BY_USER('TriggeredByUser'),
    TRIGGERED_BY_REMOTE('TriggeredByRemote'),
    TRIGGERED_BY_UPSTREAM('TriggeredByUpstream'),
    TRIGGERED_BY_CLI('TriggeredByCli')

    final String value

    BuildStatusType(String value) {
        this.value = value
    }
}
