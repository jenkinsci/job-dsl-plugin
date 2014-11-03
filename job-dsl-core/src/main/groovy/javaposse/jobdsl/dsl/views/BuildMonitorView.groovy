package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.View

import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.helpers.ContextHelper.executeInContext

class BuildMonitorView extends View {
    private final Set<String> jobNames = []

    void jobs(Closure jobsClosure) {
        JobsContext context = new JobsContext()
        executeInContext(jobsClosure, context)

        this.jobNames.addAll(context.jobNames)

        execute {
            it / 'jobNames' {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : this.jobNames.sort(CASE_INSENSITIVE_ORDER)) {
                    string(job)
                }
            }
            if (context.regex) {
                it / includeRegex(context.regex)
            }
        }
    }

    @Override
    protected String getTemplate() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>
    <owner class="hudson" reference="../../.."/>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames/>
    <jobFilters/>
    <columns/>
    <recurse>false</recurse>
    <order class="com.smartcodeltd.jenkinsci.plugins.buildmonitor.order.ByName"/>
</com.smartcodeltd.jenkinsci.plugins.buildmonitor.BuildMonitorView>'''
    }

    static enum StatusFilter {
        ALL, ENABLED, DISABLED
    }
}
