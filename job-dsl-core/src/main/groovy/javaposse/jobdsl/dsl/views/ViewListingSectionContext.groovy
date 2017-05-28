package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument
import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class ViewListingSectionContext extends SectionContext {
    int columns = 1
    Set<String> viewNames = []

    ViewListingSectionContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the number of columns of the section.
     */
    void columns(int columns) {
        checkArgument(columns > 0, 'columns must be positive integer')
        this.columns = columns
    }

    /**
     * Adds views to the section. Can be called multiple times to added more views.
     */
    void view(String viewName) {
        checkNotNull(viewName, 'viewName must not be null')

        this.viewNames.add(viewName)
    }

    /**
     * Adds views to the section. Can be called multiple times to added more views.
     */
    void views(String... viewNames) {
        for (String viewName : viewNames) {
            view(viewName)
        }
    }
}
