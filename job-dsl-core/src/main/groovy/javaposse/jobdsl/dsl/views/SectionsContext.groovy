package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.DslContext

import static java.lang.String.CASE_INSENSITIVE_ORDER
import static javaposse.jobdsl.dsl.ContextHelper.executeInContext

class SectionsContext implements Context {
    List<Node> sectionNodes = []

    /**
     * <hudson.plugins.sectioned__view.ListViewSection>
     *     <jobNames>
     *         <comparator class="hudson.util.CaseInsensitiveComparator"/>
     *         <string>test</string>
     *     </jobNames>
     *     <jobFilters/>
     *     <name>foo</name>
     *     <includeRegex>bla</includeRegex>
     *     <width>FULL</width>
     *     <alignment>CENTER</alignment>
     *     <columns>
     *         <hudson.views.StatusColumn/>
     *         <hudson.views.WeatherColumn/>
     *         <hudson.views.JobColumn/>
     *         <hudson.views.LastSuccessColumn/>
     *         <hudson.views.LastFailureColumn/>
     *         <hudson.views.LastDurationColumn/>
     *         <hudson.views.BuildButtonColumn/>
     *     </columns>
     * </hudson.plugins.sectioned__view.ListViewSection>
     */
    void listView(@DslContext(ListViewSectionContext) Closure listViewSectionClosure) {
        ListViewSectionContext context = new ListViewSectionContext()
        executeInContext(listViewSectionClosure, context)

        sectionNodes << new NodeBuilder().'hudson.plugins.sectioned__view.ListViewSection' {
            jobNames {
                comparator(class: 'hudson.util.CaseInsensitiveComparator')
                for (String job : context.jobsContext.jobNames.sort(true, CASE_INSENSITIVE_ORDER)) { // see GROOVY-6900
                    string(job)
                }
            }
            jobFilters()
            name(context.name)
            if (context.jobsContext.regex) {
                includeRegex(context.jobsContext.regex)
            }
            width(context.width)
            alignment(context.alignment)
            columns(context.columnsContext.columnNodes)
        }
    }
}
