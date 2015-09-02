This is the in-depth documentation of the methods available on inside the _view_ part of the DSL.

## List View

```groovy
listView(String name) { // since 1.30
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // list view options
    statusFilter(StatusFilter filter)
    jobs {
        name(String jobName)
        names(String... jobNames)
        regex(String regex)
    }
    jobFilters { // since 1.29
        regex {
            matchType(MatchType matchType) // what to do with matching jobs
            matchValue(RegexMatchValue matchValue) // what to match
            regex(String regex) // the regular expression to match against
        }
        status {
            matchType(MatchType matchType) // what to do with matching jobs
            status(Status... status) // Status to match
        }
    }
    columns {
        status()
        weather()
        name()
        lastSuccess()
        lastFailure()
        lastDuration()
        buildButton()
        lastBuildConsole() // since 1.23, requires the Extra Columns Plugin
        configureProject() // since 1.31, requires the Extra Columns Plugin
        claim()            // since 1.29, requires the Claim Plugin
        lastBuildNode()    // since 1.31, requires the Build Node Column Plugin
        categorizedJob()   // since 1.31, requires the Categorized Jobs View Plugin
        robotResults()     // since 1.33, requires the Robot Framework Plugin
        customIcon()       // since 1.29, requires the Custom Job Icon Plugin
    }
    recurse(boolean shouldRecurse = true) // since 1.31
}
view(type: ListView, Closure viewClosure) // since 1.21, deprecated since 1.30
```

Create a view which shows jobs in a list format. Details about the options can be found below. Similar to jobs, the view DSL can be extended using a [[configure block|The Configure Block]].

```groovy
listView('project-A') {
    description('All unstable jobs for project A')
    filterBuildQueue()
    filterExecutors()
    jobs {
        name('release-projectA')
        regex('project-A-.+')
    }
    jobFilters {
        status {
            status(Status.UNSTABLE)
        }
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

## Build Pipeline View

```groovy
buildPipelineView(String name) {  // since 1.30
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

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
    startsWithParameters(boolean startsWithParameters = true) // since 1.26
}
view(type: BuildPipelineView, Closure viewClosure) // since 1.21, deprecated since 1.30
```

Create a view of upstream and downstream connected jobs. Details about the options can be found below. Requires the [Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin).

```groovy
buildPipelineView('project-A') {
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

## Sectioned View

```groovy
sectionedView(String name) {  // since 1.30
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // sections view options
    sections {
        listView(Closure listViewSectionClosure)
    }
}
view(type: SectionedView, Closure viewClosure) // since 1.25, deprecated since 1.30
```

Create a view that can be divided into sections. Details about the options can be found below. Requires the
[Sectioned View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Sectioned+View+Plugin).

```groovy
sectionedView('project-summary') {
    filterBuildQueue()
    filterExecutors()
    sections {
        listView {
            name('Project A')
            jobs {
                regex('project-A-.*')
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
        listView {
            name('Project B')
            jobs {
                regex('project-B-.*')
            }
            jobFilters {
                regex {
                    matchValue(RegexMatchValue.DESCRIPTION)
                    regex('.*-project-B-.*')
                }
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
    }
}
```

## Nested View

```groovy
nestedView(String name) { // since 1.30
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // nested view options
    views {
        listView(String name, Closure closure = null)             // since 1.30
        sectionedView(String name, Closure closure = null)        // since 1.30
        nestedView(String name, Closure closure = null)           // since 1.30
        deliveryPipelineView(String name, Closure closure = null) // since 1.30
        buildPipelineView(String name, Closure closure = null)    // since 1.30
        buildMonitorView(String name, Closure closure = null)     // since 1.30
        categorizedJobsView(String name, Closure closure = null)  // since 1.31
        view(Map<String, Object> arguments = [:], String name, Closure viewClosure) // since 1.30, deprecated since 1.31
        view(Map<String, Object> arguments = [:], Closure viewClosure) // deprecated since 1.30
    }
    columns {
        status()
        weather()
    }
}
view(type: NestedView, Closure viewClosure) // since 1.25, deprecated since 1.30
```

Create a view that allows grouping views into multiple levels. Details about the options can be found below. Requires
the [Nested View Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Nested+View+Plugin).

```groovy
nestedView('project-a') {
    views {
        listView('overview') {
            jobs {
                regex('project-A-.*')
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
        buildPipelineView('pipeline') {
            selectedJob('project-a-compile')
        }
    }
}
```

## Delivery Pipeline View

```groovy
deliveryPipelineView(String name) {  // since 1.30
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // delivery pipeline view options
    pipelineInstances(int number)
    showAggregatedPipeline(boolean showAggregatedPipeline = true)
    columns(int number)
    sorting(Sorting sorting)
    updateInterval(int seconds)
    enableManualTriggers(boolean enable = true)
    showAvatars(boolean showAvatars = true)
    showChangeLog(boolean showChangeLog = true)
    showTotalBuildTime(boolean value = true)    // since 1.38
    allowRebuild(boolean value = true)          // since 1.38
    allowPipelineStart(boolean value = true)    // since 1.38
    showDescription(boolean value = true)       // since 1.38
    showPromotions(boolean value = true)        // since 1.38
    pipelines {
        component(String name, String initialJob)
        regex(String regex)
    }
}
view(type: DeliveryPipelineView, Closure viewClosure) // since 1.26, deprecated since 1.30
```

Create a view that renders pipelines based on upstream/downstream jobs. Details about the options can be found below.
Requires the [Delivery Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Delivery+Pipeline+Plugin).

```groovy
deliveryPipelineView('project-a') {
    pipelineInstances(5)
    showAggregatedPipeline()
    columns(2)
    sorting(Sorting.TITLE)
    updateInterval(60)
    enableManualTriggers()
    showAvatars()
    showChangeLog()
    pipelines {
        component('Sub System A', 'compile-a')
        component('Sub System B', 'compile-b')
        regex(/compile-subsystem-(.*)/)
    }
}
```

## Build Monitor View

```groovy
buildMonitorView(String name) {  // since 1.28
    // common options
    name(String name) // deprecated since 1.30
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // build monitor view options
    statusFilter(StatusFilter filter)
    jobs {
        name(String jobName)
        names(String... jobNames)
        regex(String regex)
    }
}
view(type: BuildMonitorView, Closure viewClosure) // since 1.28, deprecated since 1.30
```

Create a view that provides a highly visible view of the status of selected Jenkins jobs. Details
about the options can be found below. Similar to jobs, the view DSL can be extended using a
[[configure block|The Configure Block]].

```groovy
buildMonitorView('project-A') {
    description('All jobs for project A')
    jobs {
        name('release-projectA')
        regex('project-A-.+')
    }
}
```

## Categorized Jobs View

```groovy
categorizedJobsView(String name) {  // since 1.31
    // common options
    description(String description)
    filterBuildQueue(boolean filterBuildQueue = true)
    filterExecutors(boolean filterExecutors = true)
    configure(Closure configureBlock)

    // list view options
    // ... (all of them)

    // categorized jobs view options
    categorizationCriteria {
        regexGroupingRule(String groupRegex, String namingRule = null)
    }
}
```

Creates a new view that is very similar to the standard Jenkins List Views, but where you can group jobs and 
categorize them according to regular expressions.
Requires the [Categorized Jobs View](https://wiki.jenkins-ci.org/display/JENKINS/Categorized+Jobs+View).

```groovy
categorizedJobsView('Configuration') {
    jobs {
        regex(/configuration_.*/)
    }

    categorizationCriteria {
        regexGroupingRule(/^configuration_([^_]+).*$/)
    }

    columns {
        status()
        categorizedJob()
        buildButton()
    }
}
```

## Common View Options

### Name

```groovy
name(String viewName) // deprecated since 1.30
```

The name of the view, required when using the deprecated `view` method without name argument.

The view name is treated as absolute to the Jenkins root by default, but the seed job can be configured to interpret
names relative to the seed job. (since 1.24)

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

### Job Filters

```groovy
listView {
    jobFilters {
        regex {
            matchType(MatchType matchType) // what to do with matching Jobs
            matchValue(RegexMatchValue matchValue) // what to match
            regex(String regex) // the regular expression to match against
        }
        status {
            matchType(MatchType matchType) // what to do with matching Jobs
            status(Status... status)
        }
    }
}
```

Adds or removes jobs from the view by specifying filters. Each filter needs to specify if it includes or excludes jobs
from the view by calling `matchType` which defaults to `MatchType.INCLUDE_MATCHED`. Requires the
[View Job Filters Plugin](https://wiki.jenkins-ci.org/display/JENKINS/View+Job+Filters).

Possible values for `matchType` are `MatchType.INCLUDE_MATCHED`, `MatchType.INCLUDE_UNMATCHED`,
`MatchType.EXCLUDE_MATCHED` or `MatchType.EXCLUDE_UNMATCHED`. Possible values for `matchValue` are
`RegexMatchValue.NAME`, `RegexMatchValue.DESCRIPTION`, `RegexMatchValue.SCM`, `RegexMatchValue.EMAIL`,
`RegexMatchValue.MAVEN`, `RegexMatchValue.SCHEDULE` or `RegexMatchValue.NODE`. Possible values for `status` are
`Status.UNSTABLE`, `Status.FAILED`, `Status.ABORTED`, `Status.DISABLED` or `Status.STABLE`.

```groovy
listView('example') {
    jobFilters {
        regex {
            matchType(MatchType.EXCLUDE_MATCHED)
            matchValue(RegexMatchValue.DESCRIPTION)
            regex('.*project-a.*')
        }
        status {
            matchType(MatchType.INCLUDE_MATCHED)
            status(Status.FAILED)
        }
    }
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
    configureProject() // since 1.31, requires the Extra Columns Plugin
    claim()            // since 1.29, requires the Claim Plugin
    lastBuildNode()    // since 1.31, requires the Build Node Column Plugin
    categorizedJob()   // since 1.31, requires the Categorized Jobs View Plugin
    robotResults()     // since 1.33, requires the Robot Framework Plugin
    customIcon()       // since 1.29, requires the Custom Job Icon Plugin
}
```

Adds columns to the views. The view will have no columns by default. Some column types require the
[Extra Columns Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Extra+Columns+Plugin) or
[Custom Job Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Custom+Job+Icon+Plugin).

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

### Pipeline Starts With Parameters

```groovy
startsWithParameters(boolean startsWithParameters = true)
```

Use this method if you want toggle the "Pipeline starts with parameters" option in the pipeline view configuration.
Optional, defaults to `false`.

Requires version 1.4.3 of the
[Build Pipeline Plugin](https://wiki.jenkins-ci.org/display/JENKINS/Build+Pipeline+Plugin).

```groovy
startsWithParameters()
```

(since 1.26)

## Sectioned View Options

### List View Section

```groovy
sectionedView {
    sections {
        listView {
            name(String name)                     // name of the section
            width(String width)                   // either FULL, HALF, THIRD or TWO_THIRDS
            alignment(String alignment)           // either CENTER, LEFT or RIGHT
            jobs(Closure jobClosure)              // see the jobs closure for list views above
            jobFilters(Closure jobFiltersClosure) // see the jobFilters closure for list views above
            columns(Closure columnsClosure)       // see the columns closure for list views above
        }
    }
}
```

Creates a section containing a list of jobs. Width defaults to `FULL` and alignment defaults to `CENTER` if not
specified.

```groovy
sectionedView('example') {
    sections {
        listView {
            name('project-A')
            width('HALF')
            alignment('LEFT')
            jobs {
                regex('project-A-.*')
            }
            columns {
                status()
                weather()
                name()
                lastSuccess()
                lastFailure()
            }
        }
    }
}
```

## Nested View Options

### Views

```groovy
nestedView {
    views {
        listView(String name, Closure closure = null)             // since 1.30
        sectionedView(String name, Closure closure = null)        // since 1.30
        nestedView(String name, Closure closure = null)           // since 1.30
        deliveryPipelineView(String name, Closure closure = null) // since 1.30
        buildPipelineView(String name, Closure closure = null)    // since 1.30
        buildMonitorView(String name, Closure closure = null)     // since 1.30
        view(Map<String, Object> arguments = [:], String name, Closure viewClosure) // since 1.30, deprecated since 1.31
        view(Map<String, Object> arguments = [:], Closure viewClosure) // deprecated since 1.30
    }
}
```

Creates the nested views. The view methods works like the top-level view method.

```groovy
nestedView('example-1') {
    views {
        listView('overview') {
            jobs {
                regex('project-A-.*')
            }
            columns {
                status()
                name()
            }
        }
        buildPipelineView('pipeline') {
            selectedJob('project-a-compile')
        }
    }
}
```

### Columns

```groovy
nestedView {
    columns {
        status()
        weather()
    }
}
```

Adds columns to the view. Only the status and weather column are supported.

## Delivery Pipeline View Options

### Pipeline Instances

```groovy
deliveryPipelineView {
    pipelineInstances(int number)
}
```

Number of pipelines instances showed for each pipeline. Optional, defaults to 3 if omitted.

```groovy
deliveryPipelineView('example') {
    pipelineInstances(5)
}
```

### Aggregated Pipeline

```groovy
deliveryPipelineView {
    showAggregatedPipeline(boolean showAggregatedPipeline = true)
}
```

Show a aggregated view where each stage shows the latest version being executed. Optional, defaults to `false` if
omitted.

```groovy
deliveryPipelineView('example') {
    showAggregatedPipeline()
}
```

### Columns

```groovy
deliveryPipelineView {
    columns(int number)
}
```

Number of columns used for showing pipelines. Optional, defaults to 1 if omitted.

```groovy
deliveryPipelineView('example') {
    columns(2)
}
```

### Sorting

```groovy
deliveryPipelineView {
    sorting(Sorting sorting)
}
```

Specifies how to sort the pipeline in the view, only applicable for several pipelines. Possible value are
`Sorting.NONE`, `Sorting.TITLE` and `Sorting.LAST_ACTIVITY`. Optional, defaults to `Sorting.NONE` if omitted.

```groovy
deliveryPipelineView('example') {
    sorting(Sorting.TITLE)
}
```

### Avatars

```groovy
deliveryPipelineView {
    showAvatars(boolean showAvatars = true)
}
```

Show avatar pictures instead of user names. Optional, defaults to `false` if omitted.

```groovy
deliveryPipelineView('example') {
    showAvatars()
}
```

### Update Interval

```groovy
deliveryPipelineView {
    updateInterval(int seconds)
}
```

Specifies how often the view will be updated. Optional, defaults to 2 if omitted.

```groovy
deliveryPipelineView('example') {
    updateInterval(60)
}
```

### Manual Triggers

```groovy
deliveryPipelineView {
    enableManualTriggers(boolean enable = true)
}
```

Show a button if a task is manual. Optional, defaults to `false` if omitted.

```groovy
deliveryPipelineView('example') {
    enableManualTriggers()
}
```

### Change Log

```groovy
deliveryPipelineView {
    showChangeLog(boolean showChangeLog = true)
}
```

Show SCM change log for the first job in the pipeline. Optional, defaults to `false` if omitted.

```groovy
deliveryPipelineView('example') {
    showChangeLog()
}
```

### Pipelines

```groovy
deliveryPipelineView {
    pipelines {
        component(String name, String initialJob)
        regex(String regex)
    }
}
```

Defines pipelines by either specifying names and start jobs or by regular expressions. Both variants can be called
multiple times to add different pipelines to the view.

```groovy
deliveryPipelineView('example-1') {
    pipelines {
        component('Sub System A', 'compile-a')
        component('Sub System B', 'compile-b')
    }
}

deliveryPipelineView('example-2') {
    pipelines {
        regex(/compile-(.*)/)
    }
}
```

## Build Monitor View Options

### Status Filter

See [Status Filter](#status-filter) in the [List View Options](#list-view-options) above.

### Jobs

See [Jobs](#jobs) in the [List View Options](#list-view-options) above.


## Categorized Jobs View Options

### Categorization Criteria

```groovy
categorizedJobsView {
    categorizationCriteria {
        regexGroupingRule(String groupRegex, String namingRule = null)
    }
}
```

Contains list of grouping rules. Currently only a rule for groups jobs using a regular expression is available.

```groovy
categorizedJobsView('example') {
    categorizationCriteria {
        regexGroupingRule(/^configuration_([^_]+).*$/)
    }
}
```
