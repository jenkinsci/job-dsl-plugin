package javaposse.jobdsl.dsl.views.jobfilter

class OtherViewsFilter extends AbstractJobFilter {
    String viewName = ''

    /**
     * Select view's name.
     */
    void viewName(String viewName) {
        this.viewName = viewName
    }
}
