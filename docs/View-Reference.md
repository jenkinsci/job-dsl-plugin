This is the in-depth documentation of the methods available on inside the _view_ part of the DSL.

## List View

```groovy
view(type: ListView) {  // since 1.21
    // common options
    name(String name)
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)

    // list view options
    statusFilter(StatusFilter filter)
    jobFilters { // since 1.23
        mostRecentJobs(int, boolean)
        unclassifiedJobs(IncludeExcludeType type)
        securedJobs(IncludeExcludeType type)
        regex(String regexp, MatchValue matchValue, IncludeExcludeType type)
        upstreamDownstream(boolean downstream, boolean upstream, boolean recursive, boolean showSource)
        jobStatusFilter { // See below for closure syntax
            includeExcludeType(IncludeExcludeType type)
            unstable()
        }
        buildStatusFilter { // See below for closure syntax
            includeExcludeType(IncludeExcludeType type)
            neverBuilt()
        }
        jobTypeFilter { // See below for closure syntax
            includeExcludeType(IncludeExcludeType type)
            jobType(JobType type)
        }
        scmTypeFilter { // See below for closure syntax
            includeExcludeType(IncludeExcludeType type)
            scmType(SCMType scm)
        }
        otherViewFilter { // See below for closure syntax
            includeExcludeType(IncludeExcludeType type)
            otherViewName(Strign otherView)
        }
    }
    jobs {
        name(String jobName)
        names(String... jobNames)
        regex(String regex)
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
    configure(Closure configureBlock)
}
```

Create a view which shows jobs in a list format. Details about the options can be found below. Similar to jobs, the view DSL can be extended using a [[configure block|The Configure Block]].

```groovy
view(type: ListView) {
    name('project-A')
    description('All jobs for project A')
    filterBuildQueue()
    filterExecutors()
    jobs {
        name('release-projectA')
        regex('project-A-.+')
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
    }
}
```

### Job Filters
#### Most Recent Jobs
```groovy
mostRecentJobs(int count, boolean useStartTime = false)
```

Restricts the view to only the specified number of recently completed jobs. The boolean argument is to use the job start time versus end time for calculation.

#### Unclassified Jobs
```groovy
unclassifiedJobs(IncludeExcludeType matchType)
```

This filter allows a View to show all Jobs that haven't already been included in another View (not counting any "All Jobs" views).

This is especially useful if you have hundreds of Jobs organized into Views, and you want an easy way to identify which Jobs haven't been "classified" into a View yet.

Because this filter looks at other Views, there is a risk of creating an infinite loop, so plan carefully.

#### Project-based Secured Jobs
```groovy
securedJobs(IncludeExcludeType matchType)
```

Filters on whether a job has selected the "Enable project-based security" option.

NOTE: This filter does not provide security - it merely provides convenient views that feed off of the security you've already set up.

#### Regualr Expression Job Filter
```groovy
regex(String regexp, MatchValue matchValue, IncludeExcludeType matchType)
```

You can use as many Regular Expression Filters as you like - they will each apply in the order you put them in.

Match Value: Choose from the following to match on
Job name
Job description
Job SCM configuration: Matching by SCM is extremely useful if you have hundreds of jobs that need to be "auto organized", and are already organized under source control. (Currently matches against SVN, Git, and CVS paths/configuration)
Email recipients: Matching by Email is useful for organizing by "who cares about this job."
Maven configuration: Use this to match against "what does this job do"
Job schedule: Matches against the chron pattern and any comments for Timer and SCM triggers.
Node label expression: Matches against the Label Expression under the "Restrict where this project can be run" option.

Match Type: Choose whether this filter will add additional jobs, or remove jobs.

Example:
```groovy
regex('.*_Nightly', JobName, INCLUDE_MATCHED)
```

#### Upstream / Downstream Jobs Filter
```groovy
upstreamDownstream(boolean includeDownstream, boolean includeUpstream, boolean recursiveInclude, boolean showSource)
```

This filter allows you to create a view consisting of jobs that are related through the concept of Upstream/Downstream (also called "Build after other projects are built" and "Build other projects").
The options provided allow you to choose exactly which types of related jobs to show.

Note that filters are chained together, so using this filter builds off of the jobs included by previous filters. Remember that
This filter will not include any jobs if there are no jobs already selected
This filter (like most other filters) is capable of removing jobs that were already selected

Example:
```groovy
upstreamDownstream(true, false, true, true)
```

#### Job Statuses Filter
```groovy
jobStatusFilter {
    includeExcludeType(IncludeExcludeType matchType)
    unstable(boolean include = true)
    failed(boolean include = true)
    aborted(boolean include = true)
    disabled(boolean include = true)
    stable(boolean include = true)
}
```

You can use as many Job Filters as you like - they will each apply in the order you put them in.
Choose an appropriate Match Type to choose whether this filter will add additional jobs, or remove jobs.

Example:
```groovy
jobStatusFilter {
    includeExcludeType(INCLUDE_MATCHED)
    unstable()
    failed()
    aborted(false)
}
```
#### Build Statuses Filter
```groovy
buildStatusFilter {
    includeExcludeType(IncludeExcludeType matchType)
    neverBuilt(boolean include = true)
    building(boolean include = true)
    inBuildQueue(boolean include = true)
}
```

You can use as many Job Filters as you like - they will each apply in the order you put them in.
Choose an appropriate Match Type to choose whether this filter will add additional jobs, or remove jobs.

Example:
```groovy
buildStatusFilter {
    includeExcludeType(EXCLUDE_MATCHED)
    neverBuilt()
}
```
#### Job Type Filter
```groovy
jobTypeFilter {
    includeExcludeType(IncludeExcludeType matchType)
    jobType(JobType type)
}
```
Filter by the type of job: ```JobType.FreeStyle, JobType.Maven, JobType.Matrix, JobType.External```


Example:
```groovy
jobTypeFilter {
    includeExcludeType(EXCLUDE_UNMATCHED)
    jobType(FreeStyle)
}
```

#### SCM Type Filter
```groovy
scmTypeFilter {
    includeExcludeType(IncludeExcludeType matchType)
    scmType(SCMType scm)
}
```

Organize views by SCM type. Especially useful in a large organization with hundreds of jobs, or when you are migrating jobs to use a new scm.

```SCMType.CVS, SCMType.CVSProjectSet, SCMType.Git, SCMType.None, SCMType.SVN```


Example:
```groovy
scmTypeFilter {
    includeExcludeType(INCLUDE_UNMATCHED)
    scmType(CVS)
}
```
#### Other Views Filter
```groovy
otherViewFilter {
    includeExcludeType(IncludeExcludeType matchType)
    otherViewName(String viewName)
}
```
This filter allows you to base one view's jobs off of other view's jobs. This is mostly helpful in large organizations with hundreds of jobs, and many views.

For example, if you have a company organizational hierarchy of projects, different configuration managers might want to view a different sub-set of jobs, and there's no reason to manage each view's list of jobs if some views are simply meant to be an aggregate of other views.

Another example is if you have one "special" view that contains utility or legacy jobs, and you want to easily exclude those jobs from other views, but there are no well-defined characteristics of those jobs that you can filter on. With this filter, you can put your effort into creating that special view, and then for other views simply choose to exclude all jobs that appear in that special view.

Do not select a view as it's own "Other View" - this can cause unpredictable behavior.
Example:
```groovy
otherViewFilter {
    includeExcludeType(EXCLUDE_UNMATCHED)
    otherViewName('some-view')
}
```
## Build Pipeline View

```groovy
view(type: BuildPipelineView) {  // since 1.21
    // common options
    name(String name)
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)

    // build pipeline view options
    displayedBuilds(int displayedBuilds)
    title(String title)
    // consoleOutputLinkStyle and customCssUrl available in version 1.4.2 of the Build Pipeline Plugin
    consoleOutputLinkStyle(OutputStyle consoleOutputLinkStyle)
    customCssUrl(String cssUrl)
    selectedJob(String selectedJob)
    triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true)
    alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true)
    showPipelineParameters(boolean showPipelineParameters = true)
    showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true)
    refreshFrequency(int seconds)
    showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true)
    configure(Closure configureBlock)
}
```

Create a view of upstream and downstream connected jobs. Details about the options can be found below. Requires the [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin).

```groovy
view(type: BuildPipelineView) {
    name('project-A')
    filterBuildQueue()
    filterExecutors()
    title('Project A CI Pipeline')
    displayedBuilds(5)
    selectedJob('project-A-compile')
    alwaysAllowManualTrigger()
    showPipelineParameters()
    refreshFrequency(60)
}
```

## Common View Options

### Name

```groovy
name(String viewName)
```

The name of the view, **required**.

```groovy
name('project-A')
```

### Description

```groovy
description(String description)
```

Sets description of the view, optional.

```groovy
description('lorem ipsum')
```

### Filter Build Queue

```groovy
filterBuildQueue(boolean filterBuildQueue = true)
```

If set to `true`. only jobs in this view will be shown in the build queue. Defaults to `false` if omitted.

```groovy
filterBuildQueue()
```

### Filter Executors

```groovy
filterExecutors(boolean filterExecutors = true)
```

If set to `true`, only those build executors will be shown that could execute the jobs in this view.  Defaults to `false` if omitted.

```groovy
filterExecutors()
```

## List View Options

### Status Filter

```groovy
statusFilter(filter)
```

Filter the job list by enabled/disabled status. Valid values are `ALL` (default), `ENABLED` and `DISABLED`.

```groovy
statusFilter(ENABLED)
```

### Jobs

```groovy
jobs {
    name(String jobName)
    names(String... jobNames)
    regex(String regex)
}
```

Adds jobs to the view. `name` and `names` can be called multiple times to added more jobs, but only the last `regex` call will be used.

```groovy
jobs {
    name('build')
    name('test')
}
```

```groovy
jobs {
    names('build', 'test')
}
```

```groovy
jobs {
    regex('project-A-.+')
}
```

### Columns

```groovy
columns {
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
    lastBuildConsole() // since 1.23, requires the Extra Columns Plugin
}
```

Adds columns to the views. The view will have no columns by default. Some column types require the
[Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin).

## Build Pipeline View Options

### Displayed Builds

```groovy
displayedBuilds(int builds)
```

Sets number of displayed builds. Optional, defaults to 1, must be greater than zero.

```groovy
displayedBuilds(5)
```

### Title

```groovy
title(String title)
```

Sets a title for the pipeline. Optional.

```groovy
title('Project A CI Pipeline')
```

### Console Output Link Style

```groovy
consoleOutputLinkStyle(OutputStyle outputStyle)
```

Defines the console output style. Options are Lightbox, NewWindow and ThisWindow.

```groovy
consoleOutputLinkStyle(OutputStyle.Lightbox)
```

### CSS URL

```groovy
customCssUrl(String cssUrl)
```

Sets a URL for custom CSS files.

```groovy
customCssUrl('url/to/custom/css/files')
```

### Selected Job

```groovy
selectedJob(String jobName)
```

Defines the first job in the pipeline.

```groovy
selectedJob('project-a-compile')
```

### Trigger Only Latest Job

```groovy
triggerOnlyLatestJob(boolean triggerOnlyLatestJob = true)
```

Use this method to restrict the display of a trigger button to only the most recent successful build pipelines. This option will also limit retries to just unsuccessful builds of the most recent build pipelines. Optional, defaults to `false`.

```groovy
triggerOnlyLatestJob()
```

### Always Allow Manual Trigger

```groovy
alwaysAllowManualTrigger(boolean alwaysAllowManualTrigger = true)
```

Use this method if you want to be able to execute a successful pipeline step again. Optional, defaults to `false`.

```groovy
alwaysAllowManualTrigger()
```

### Show Pipeline Parameters

```groovy
showPipelineParameters(boolean showPipelineParameters = true)
```

Use this method if you want to display the parameters used to run the first job in each pipeline's revision box. Optional, defaults to `false`.

```groovy
showPipelineParameters()
```

### Show Pipeline Parameters In Headers

```groovy
showPipelineParametersInHeaders(boolean showPipelineParametersInHeaders = true)
```

Use this method if you want to display the parameters used to run the latest successful job in the pipeline's project headers. Optional, defaults to `false`.

```groovy
showPipelineParametersInHeaders()
```

### Refresh Frequency

```groovy
refreshFrequency(int refreshFrequency)
```

Frequency at which the Build Pipeline Plugin updates the build cards in seconds. Optional, defaults to `3`.

```groovy
refreshFrequency(60)
```

### Show Pipeline Definition Header

```groovy
showPipelineDefinitionHeader(boolean showPipelineDefinitionHeader = true)
```

Use this method if you want to show the pipeline definition header in the pipeline view. Optional, defaults to `false`.

```groovy
showPipelineDefinitionHeader()
```
