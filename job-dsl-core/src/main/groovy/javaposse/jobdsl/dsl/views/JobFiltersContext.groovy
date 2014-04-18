package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.helpers.Context
import javaposse.jobdsl.dsl.views.jobfilters.BuildStatusFilterContext
import javaposse.jobdsl.dsl.views.jobfilters.IncludeExcludeType
import javaposse.jobdsl.dsl.views.jobfilters.JobStatusFilterContext

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class JobFiltersContext implements Context {
    List<Node> filterNodes = []

    void jobStatusFilter(IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED, Closure statusClosure) {
        JobStatusFilterContext statusFilterContext = new JobStatusFilterContext(type)
        executeInContext(statusClosure, statusFilterContext)

        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.JobStatusFilter' {
            includeExcludeTypeString(statusFilterContext.includeExcludeType)
            unstable(statusFilterContext.unstable)
            failed(statusFilterContext.failed)
            aborted(statusFilterContext.aborted)
            disabled(statusFilterContext.disabled)
            stable(statusFilterContext.stable)
        }
        filterNodes << filterNode
    }

    void buildStatusFilter(IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED, Closure statusClosure) {
        BuildStatusFilterContext statusFilterContext = new BuildStatusFilterContext()
        executeInContext(statusClosure, statusFilterContext)

        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.BuildStatusFilter' {
            includeExcludeTypeString(type.value)
            neverBuilt(statusFilterContext.neverBuilt)
            building(statusFilterContext.building)
            inBuildQueue(statusFilterContext.inBuildQueue)
        }
        filterNodes << filterNode
    }

    void jobTypeFilter(JobType type, IncludeExcludeType matchType = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.JobTypeFilter' {
            includeExcludeTypeString(matchType.value)
            jobType(type.value)
        }
        filterNodes << filterNode
    }

    void scmTypeFilter(SCMType scm, IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.ScmTypeFilter' {
            includeExcludeTypeString(type.value)
            scmType(scm.value)
        }
        filterNodes << filterNode
    }

    void otherViewFilter(String otherView, IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.OtherViewsFilter' {
            includeExcludeTypeString(type.value)
            otherViewName(otherView)
        }
        filterNodes << filterNode
    }

    /**
     * Filters to the most recent N number of jobs
     * @param count the number of jobs to include
     * @param useStartTime Use start time instead of completion time for the calculation
     */
    void mostRecentJobs(int count, boolean useStartTime = false) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.MostRecentJobsFilter' {
            maxToInclude(count)
            checkStartTime(useStartTime)
        }
        filterNodes << filterNode
    }

    void unclassifiedJobs(IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.UnclassifiedJobsFilter' {
            includeExcludeTypeString(type.value)
        }
        filterNodes << filterNode
    }

    void securedJobs(IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.SecuredJobsFilter' {
            includeExcludeTypeString(type.value)
        }
        filterNodes << filterNode
    }

    /**
     * This filter allows you to match a regular expressions against various attributes. These are determined by the match argument.
     * @param regexString The regular expression to use for matching
     * @param match What attribute to match the regex against
     * @param type Determine what to include (matched/unmatched, inclusive/exclusive)
     */
    void regex(String regexString, MatchValue match, IncludeExcludeType type = IncludeExcludeType.INCLUDE_MATCHED) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.RegExJobFilter' {
            includeExcludeTypeString(type.value)
            valueTypeString(match.value)
            regex(regexString)
        }
        filterNodes << filterNode
    }

    /**
     * This filter allows you to create a view consisting of jobs that are related through the concept of Upstream/Downstream 
     * (also called "Build after other projects are built" and "Build other projects").
     * The options provided allow you to choose exactly which types of related jobs to show.
     * @param downstream Include downstream Jobs?
     * @param upstream Include upstream Jobs?
     * @param recursiveInclude Include upstream/downstream jobs recursively
     * @param showSource Do not show source jobs
     */
    void upstreamDownstream(boolean downstream, boolean upstream, boolean recursiveInclude, boolean showSource) {
        def nodeBuilder = new NodeBuilder()
        Node filterNode = nodeBuilder.'hudson.views.UpstreamDownstreamJobsFilter' {
            includeDownstream(downstream)
            includeUpstream(upstream)
            recursive(recursiveInclude)
            excludeOriginals(showSource)
        }
        filterNodes << filterNode
    }

    /**
     *  An enum used as the second argument for the regex filter. Used to determine what the regex is matched against.
     */
    static enum MatchValue {
        JobName('NAME'), JobDescription('DESCRIPTION'), JobSCMConfig('SCM'), EmailRecipients('EMAIL'), MavenConfig('MAVEN'), JobSchedule('SCHEDULE'), NodeLabelExpression('NODE')

        final String value

        MatchValue(String value) {
            this.value = value
        }
    }

    static enum SCMType {
        CVS('hudson.scm.CVSSCM'), CVSProjectSet('hudson.scm.CvsProjectset'), Git('hudson.plugins.git.GitSCM'), None('hudson.scm.NullSCM'), SVN('hudson.scm.SubversionSCM')

        final String value

        SCMType(String value) {
            this.value = value
        }
    }

    static enum JobType {
        FreeStyle('hudson.model.FreeStyleProject'), Maven('hudson.maven.MavenModuleSet'), Matrix('hudson.matrix.MatrixProject'), External('hudson.model.ExternalJob')

        final String value

        JobType(String value) {
            this.value = value
        }
    }
}
