package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.DslContext
import javaposse.jobdsl.dsl.JobManagement

import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class SectionsContext extends AbstractContext {
    List<Node> sectionNodes = []

    SectionsContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /*
     * Adds a generic section.
     */
    private void generic(String type, @DslContext(SectionContext) Closure sectionClosure) {
        SectionContext context = new SectionContext(jobManagement)
        executeInContext(sectionClosure, context)

        sectionNodes << new NodeBuilder()."$type" {
            jobNames {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : context.jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(job)
                }
            }
            jobFilters(context.jobFiltersContext.filterNodes)
            name(context.name)
            if (context.jobsContext.regex) {
                includeRegex(context.jobsContext.regex)
            }
            width(context.width)
            alignment(context.alignment)
        }
    }

    /**
     * Adds a job graphs section.
     *
     * @since 1.64
     */
    void jobGraphs(@DslContext(SectionContext) Closure sectionClosure) {
        generic('hudson.plugins.sectioned__view.JobGraphsSection', sectionClosure)
    }

    /**
     * Adds a list view section.
     */
    void listView(@DslContext(ListViewSectionContext) Closure listViewSectionClosure) {
        ListViewSectionContext context = new ListViewSectionContext(jobManagement)
        executeInContext(listViewSectionClosure, context)

        sectionNodes << new NodeBuilder().'hudson.plugins.sectioned__view.ListViewSection' {
            jobNames {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : context.jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(job)
                }
            }
            jobFilters(context.jobFiltersContext.filterNodes)
            name(context.name)
            if (context.jobsContext.regex) {
                includeRegex(context.jobsContext.regex)
            }
            width(context.width)
            alignment(context.alignment)
            columns(context.columnsContext.columnNodes)
        }
    }

    /**
     * Adds a test result section.
     *
     * @since 1.64
     */
    void testResult(@DslContext(SectionContext) Closure sectionClosure) {
        generic('hudson.plugins.sectioned__view.TestResultViewSection', sectionClosure)
    }

    /**
     * Adds a text view section.
     *
     * @since 1.64
     */
    void text(@DslContext(TextSectionContext) Closure textSectionClosure) {
        TextSectionContext context = new TextSectionContext(jobManagement)
        executeInContext(textSectionClosure, context)

        sectionNodes << new NodeBuilder().'hudson.plugins.sectioned__view.TextSection' {
            jobNames {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : context.jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(job)
                }
            }
            jobFilters(context.jobFiltersContext.filterNodes)
            name(context.name)
            if (context.jobsContext.regex) {
                includeRegex(context.jobsContext.regex)
            }
            width(context.width)
            alignment(context.alignment)
            text(context.text)
            style(context.style)
        }
    }

    /**
     * Adds a view listing section.
     *
     * @since 1.64
     */
    void viewListing(@DslContext(ViewListingSectionContext) Closure viewListingSectionClosure) {
        ViewListingSectionContext context = new ViewListingSectionContext(jobManagement)
        executeInContext(viewListingSectionClosure, context)

        sectionNodes << new NodeBuilder().'hudson.plugins.sectioned__view.ViewListingSection' {
            jobNames {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : context.jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(job)
                }
            }
            jobFilters(context.jobFiltersContext.filterNodes)
            name(context.name)
            if (context.jobsContext.regex) {
                includeRegex(context.jobsContext.regex)
            }
            width(context.width)
            alignment(context.alignment)
            views {
                for (String view : context.viewNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(view)
                }
            }
            columns(context.columns)
        }
    }
}
