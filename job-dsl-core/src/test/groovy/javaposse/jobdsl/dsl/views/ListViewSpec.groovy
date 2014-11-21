package javaposse.jobdsl.dsl.views

import spock.lang.Specification

import static javaposse.jobdsl.dsl.views.JobFiltersContext.JobType.FreeStyle
import static javaposse.jobdsl.dsl.views.JobFiltersContext.MatchValue.JobName
import static javaposse.jobdsl.dsl.views.JobFiltersContext.MatchValue.JobDescription
import static javaposse.jobdsl.dsl.views.JobFiltersContext.SCMType.CVS
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ALL
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.DISABLED
import static javaposse.jobdsl.dsl.views.ListView.StatusFilter.ENABLED
import static javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType.INCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType.INCLUDE_UNMATCHED
import static javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType.EXCLUDE_MATCHED
import static javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType.EXCLUDE_UNMATCHED


import static org.custommonkey.xmlunit.XMLUnit.compareXML
import static org.custommonkey.xmlunit.XMLUnit.setIgnoreWhitespace

class ListViewSpec extends Specification {
    ListView view = new ListView()

    def 'defaults'() {
        when:
        String xml = view.xml

        then:
        setIgnoreWhitespace(true)
        compareXML(defaultXml, xml).similar()
    }

    def 'statusFilter ALL'() {
        when:
        view.statusFilter(ALL)

        then:
        Node root = view.node
        root.statusFilter.size() == 0
    }

    def 'statusFilter ALL remove previous statusFilter'() {
        when:
        view.statusFilter(ENABLED)
        view.statusFilter(ALL)

        then:
        Node root = view.node
        root.statusFilter.size() == 0
    }

    def 'statusFilter ENABLED'() {
        when:
        view.statusFilter(ENABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'true'
    }

    def 'statusFilter DISABLED'() {
        when:
        view.statusFilter(DISABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'false'
    }

    def 'statusFilter creates only one node'() {
        when:
        view.statusFilter(DISABLED)
        view.statusFilter(ENABLED)
        view.statusFilter(DISABLED)

        then:
        Node root = view.node
        root.statusFilter.size() == 1
        root.statusFilter[0].text() == 'false'
    }

    def 'statusFilter null'() {
        when:
        view.statusFilter(null)

        then:
        thrown(NullPointerException)
    }

    def 'add job by name'() {
        when:
        view.jobs {
            name('foo')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 1
        root.jobNames[0].string[0].text() == 'foo'
    }

    def 'job name null'() {
        when:
        view.jobs {
            name(null)
        }

        then:
        thrown(NullPointerException)
    }

    def 'add jobs by name'() {
        when:
        view.jobs {
            names('foo', 'bar')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 2
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
    }

    def 'job names null'() {
        when:
        view.jobs {
            names('foo', null)
        }

        then:
        thrown(NullPointerException)

    }

    def 'add jobs by regex'() {
        when:
        view.jobs {
            regex('test')
        }

        then:
        Node root = view.node
        root.includeRegex.size() == 1
        root.includeRegex[0].text() == 'test'
    }

    def 'call jobs twice'() {
        when:
        view.jobs {
            name('foo')
        }
        view.jobs {
            name('bar')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 2
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
    }

    def 'call jobs complex'() {
        when:
        view.jobs {
            name('foo')
            names('bar', 'other')
            regex('test')
        }

        then:
        Node root = view.node
        root.jobNames.size() == 1
        root.jobNames[0].string.size() == 3
        root.jobNames[0].string[0].text() == 'bar'
        root.jobNames[0].string[1].text() == 'foo'
        root.jobNames[0].string[2].text() == 'other'
        root.includeRegex.size() == 1
        root.includeRegex[0].text() == 'test'
    }

    def 'empty columns'() {
        when:
        view.columns {
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].children.size() == 0
    }

    def 'add all columns'() {
        when:
        view.columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 7
        root.columns[0].value()[0].name() == 'hudson.views.StatusColumn'
        root.columns[0].value()[1].name() == 'hudson.views.WeatherColumn'
        root.columns[0].value()[2].name() == 'hudson.views.JobColumn'
        root.columns[0].value()[3].name() == 'hudson.views.LastSuccessColumn'
        root.columns[0].value()[4].name() == 'hudson.views.LastFailureColumn'
        root.columns[0].value()[5].name() == 'hudson.views.LastDurationColumn'
        root.columns[0].value()[6].name() == 'hudson.views.BuildButtonColumn'
    }

    def 'call columns twice'() {
        when:
        view.columns {
            name()
        }
        view.columns {
            buildButton()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 2
        root.columns[0].value()[0].name() == 'hudson.views.JobColumn'
        root.columns[0].value()[1].name() == 'hudson.views.BuildButtonColumn'
    }

    def 'lastBuildConsole column'() {
        when:
        view.columns {
            lastBuildConsole()
        }

        then:
        Node root = view.node
        root.columns.size() == 1
        root.columns[0].value().size() == 1
        root.columns[0].value()[0].name() == 'jenkins.plugins.extracolumns.LastBuildConsoleColumn'
    }

    def 'jobStatusfilter with includeMatched'() {
        when:
        view.jobFilters {
            jobStatusFilter(INCLUDE_MATCHED) {
                unstable()
            }
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.JobStatusFilter'
        root.jobFilters.'hudson.views.JobStatusFilter'.includeExcludeTypeString[0].text() == 'includeMatched'
        root.jobFilters.'hudson.views.JobStatusFilter'.unstable[0].text() == 'true'
        root.jobFilters.'hudson.views.JobStatusFilter'.failed[0].text() == 'false'
        root.jobFilters.'hudson.views.JobStatusFilter'.stable[0].text() == 'false'
        root.jobFilters.'hudson.views.JobStatusFilter'.aborted[0].text() == 'false'
        root.jobFilters.'hudson.views.JobStatusFilter'.disabled[0].text() == 'false'
    }

    def 'buildStatusfilter with excludeMatched'() {
        when:
        view.jobFilters {
            buildStatusFilter(EXCLUDE_MATCHED) {
                neverBuilt()
            }
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.BuildStatusFilter'
        root.jobFilters.'hudson.views.BuildStatusFilter'.includeExcludeTypeString[0].text() == 'excludeMatched'
        root.jobFilters.'hudson.views.BuildStatusFilter'.neverBuilt[0].text() == 'true'
        root.jobFilters.'hudson.views.BuildStatusFilter'.building[0].text() == 'false'
        root.jobFilters.'hudson.views.BuildStatusFilter'.inBuildQueue[0].text() == 'false'
    }

    def 'jobTypeFilter'() {
        when:
        view.jobFilters {
            jobTypeFilter(FreeStyle, EXCLUDE_UNMATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.JobTypeFilter'
        root.jobFilters.'hudson.views.JobTypeFilter'.includeExcludeTypeString[0].text() == 'excludeUnmatched'
        root.jobFilters.'hudson.views.JobTypeFilter'.jobType[0].text() == 'hudson.model.FreeStyleProject'
    }

    def 'scmTypeFilter'() {
        when:
        view.jobFilters {
            scmTypeFilter(CVS, INCLUDE_UNMATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.ScmTypeFilter'
        root.jobFilters.'hudson.views.ScmTypeFilter'.includeExcludeTypeString[0].text() == 'includeUnmatched'
        root.jobFilters.'hudson.views.ScmTypeFilter'.scmType[0].text() == 'hudson.scm.CVSSCM'
    }

    def 'otherViewFilter'() {
        when:
        view.jobFilters {
            otherViewFilter('Some Other View')
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.OtherViewsFilter'
        root.jobFilters.'hudson.views.OtherViewsFilter'.includeExcludeTypeString[0].text() == 'includeMatched'
        root.jobFilters.'hudson.views.OtherViewsFilter'.otherViewName[0].text() == 'Some Other View'
    }

    def 'otherViewFilter'() {
        when:
        view.jobFilters {
            otherViewFilter('Some Other View', EXCLUDE_UNMATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.OtherViewsFilter'
        root.jobFilters.'hudson.views.OtherViewsFilter'.includeExcludeTypeString[0].text() == 'excludeUnmatched'
        root.jobFilters.'hudson.views.OtherViewsFilter'.otherViewName[0].text() == 'Some Other View'
    }

    def 'mostRecentJobs'() {
        when:
        view.jobFilters {
            mostRecentJobs(5, true)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.MostRecentJobsFilter'
        root.jobFilters.'hudson.views.MostRecentJobsFilter'.maxToInclude[0].text() == '5'
        root.jobFilters.'hudson.views.MostRecentJobsFilter'.checkStartTime[0].text() == 'true'
    }

    def 'unclassifiedJobs'() {
        when:
        view.jobFilters {
            unclassifiedJobs()
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.UnclassifiedJobsFilter'
        root.jobFilters.'hudson.views.UnclassifiedJobsFilter'.includeExcludeTypeString[0].text() == 'includeMatched'
    }

    def 'unclassifiedJobs with non-default match type'() {
        when:
        view.jobFilters {
            unclassifiedJobs(EXCLUDE_MATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.UnclassifiedJobsFilter'
        root.jobFilters.'hudson.views.UnclassifiedJobsFilter'.includeExcludeTypeString[0].text() == 'excludeMatched'
    }

    def 'securedJobs'() {
        when:
        view.jobFilters {
            securedJobs()
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.SecuredJobsFilter'
        root.jobFilters.'hudson.views.SecuredJobsFilter'.includeExcludeTypeString[0].text() == 'includeMatched'
    }

    def 'securedJobs with non-default match type'() {
        when:
        view.jobFilters {
            securedJobs(INCLUDE_UNMATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.SecuredJobsFilter'
        root.jobFilters.'hudson.views.SecuredJobsFilter'.includeExcludeTypeString[0].text() == 'includeUnmatched'
    }

    def 'regex with default match type'() {
        when:
        view.jobFilters {
            regex('.*_Nightly', JobName)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.RegExJobFilter'
        root.jobFilters.'hudson.views.RegExJobFilter'.includeExcludeTypeString[0].text() == 'includeMatched'
        root.jobFilters.'hudson.views.RegExJobFilter'.valueTypeString[0].text() == 'NAME'
        root.jobFilters.'hudson.views.RegExJobFilter'.regex[0].text() == '.*_Nightly'
    }

    def 'regex with non-default match type'() {
        when:
        view.jobFilters {
            regex('.*_Nightly', JobDescription, EXCLUDE_MATCHED)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.RegExJobFilter'
        root.jobFilters.'hudson.views.RegExJobFilter'.includeExcludeTypeString[0].text() == 'excludeMatched'
        root.jobFilters.'hudson.views.RegExJobFilter'.valueTypeString[0].text() == 'DESCRIPTION'
        root.jobFilters.'hudson.views.RegExJobFilter'.regex[0].text() == '.*_Nightly'
    }

    def 'upstreamDownstream'() {
        when:
        view.jobFilters {
            upstreamDownstream(true, false, true, true)
        }

        then:
        Node root = view.getNode()
        root.jobFilters.size() == 1
        root.jobFilters[0].value().size() == 1
        root.jobFilters[0].value()[0].name() == 'hudson.views.UpstreamDownstreamJobsFilter'
        root.jobFilters.'hudson.views.UpstreamDownstreamJobsFilter'.includeDownstream[0].text() == 'true'
        root.jobFilters.'hudson.views.UpstreamDownstreamJobsFilter'.includeUpstream[0].text() == 'false'
        root.jobFilters.'hudson.views.UpstreamDownstreamJobsFilter'.recursive[0].text() == 'true'
        root.jobFilters.'hudson.views.UpstreamDownstreamJobsFilter'.excludeOriginals[0].text() == 'true'
    }

    def defaultXml = '''<?xml version='1.0' encoding='UTF-8'?>
<hudson.model.ListView>
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames class="tree-set">
        <comparator class="hudson.util.CaseInsensitiveComparator"/>
    </jobNames>
    <jobFilters/>
    <columns/>
</hudson.model.ListView>'''
}
