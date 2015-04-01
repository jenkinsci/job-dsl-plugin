package javaposse.jobdsl.dsl.views

class CategorizedJobsViewSpec extends ListViewSpec {

    public static final String GROUPING_RULE_NODE_NAME = 'org.jenkinsci.plugins.categorizedview.GroupingRule'

    def setup() {
        view = new CategorizedJobsView(jobManagement)
    }

    protected String getDefaultXml() {
        '''<?xml version='1.0' encoding='UTF-8'?>
<org.jenkinsci.plugins.categorizedview.CategorizedJobsView plugin="categorized-view">
    <filterExecutors>false</filterExecutors>
    <filterQueue>false</filterQueue>
    <properties class="hudson.model.View$PropertyList"/>
    <jobNames>
        <comparator class="hudson.util.CaseInsensitiveComparator"/>
    </jobNames>
    <jobFilters/>
    <columns/>
    <groupingRules/>
    <categorizationCriteria/>
</org.jenkinsci.plugins.categorizedview.CategorizedJobsView>'''
    }

    def 'should do nothing on empty categorization criteria'() {
        when:
        ((CategorizedJobsView) view).categorizationCriteria { }

        then:
        view.node.categorizationCriteria.size() == 1
        view.node.categorizationCriteria.get(0).value().size() == 0
    }

    def 'should add group by full closure'() {
        when:
        ((CategorizedJobsView) view).categorizationCriteria {
            groupingRule {
                groupRegex('regex')
                namingRule('naming')
            }
        }

        and:
        def node = view.node.categorizationCriteria.get(0)

        then:
        node.value().size() == 1
        node.value().get(0).name() == GROUPING_RULE_NODE_NAME
        node.value().get(0).value().size() == 2
    }

    def 'should add group by regex with naming'() {
        when:
        ((CategorizedJobsView) view).categorizationCriteria {
            byRegexWithNaming('regex', 'naming')
        }

        and:
        def node = view.node.categorizationCriteria.get(0)

        then:
        node.value().size() == 1
        node.value().get(0).name() == GROUPING_RULE_NODE_NAME
    }

    def 'should use groupRegex and namingRule names for alias'() {
        given:
        def rule = new CategorizationCriteriaContext(jobManagement)

        when:
        rule.byRegexWithNaming('regex', 'naming')

        and:
        def content = rule.groupingRules.get(0).value()

        then:
        content.size() == 2
        content.get(0).name() == 'groupRegex'
        content.get(0).value() == 'regex'
        content.get(1).name() == 'namingRule'
        content.get(1).value() == 'naming'

    }

    def 'should add more than one group'() {
        when:
        ((CategorizedJobsView) view).categorizationCriteria {
            byRegexWithNaming('regex', 'naming')
            byRegexWithNaming('regex', 'naming')
        }

        and:
        def node = view.node.categorizationCriteria.get(0)

        then:
        node.value().size() == 2
    }
}
