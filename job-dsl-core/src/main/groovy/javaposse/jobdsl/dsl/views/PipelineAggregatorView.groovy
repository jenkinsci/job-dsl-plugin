package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.NoDoc
import javaposse.jobdsl.dsl.View

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNullOrEmpty

class PipelineAggregatorView extends View {
    PipelineAggregatorView(JobManagement jobManagement, String name) {
        super(jobManagement, name)
    }

    /**
     * Set the number of most recent builds to display. Defaults to {@code 16}.
     */
    void buildHistorySize(int buildHistorySize) {
        checkArgument(buildHistorySize > 0, 'buildHistorySize must be positive integer')

        configure {
            it / methodMissing('buildHistorySize', buildHistorySize)
        }
    }

    /**
     * Set regular expression used for filtering pipelines to be displayed.
     */
    void filterRegex(String filterRegex) {
        checkNotNullOrEmpty(filterRegex, 'filterRegex must be specified')

        configure {
            it / methodMissing('filterRegex', filterRegex)
        }
    }

    /**
     * Set font size. Defaults to {@code 16}.
     */
    void fontSize(int fontSize) {
        checkArgument(fontSize > 0, 'fontSize must be positive integer')

        configure {
            it / methodMissing('fontSize', fontSize)
        }
    }

    /**
     * Display only last pipeline build. Defaults to {@code false}.
     */
    void onlyLastBuild(boolean onlyLastBuild = true) {
        configure {
            it / methodMissing('onlyLastBuild', onlyLastBuild)
        }
    }

    /**
     * Use condensed tables. Defaults to {@code false}.
     */
    void useCondensedTables(boolean useCondensedTables = true) {
        configure {
            it / methodMissing('useCondensedTables', useCondensedTables)
        }
    }

    /**
     * Use scrolling commits. Defaults to {@code false}.
     */
    void useScrollingCommits(boolean useScrollingCommits = true) {
        configure {
            it / methodMissing('useScrollingCommits', useScrollingCommits)
        }
    }

    @Override
    @NoDoc
    void filterBuildQueue(boolean filterBuildQueue = true) {
        super.filterBuildQueue(filterBuildQueue)
    }

    @Override
    @NoDoc
    void filterExecutors(boolean filterExecutors = true) {
        super.filterExecutors(filterExecutors)
    }
}
