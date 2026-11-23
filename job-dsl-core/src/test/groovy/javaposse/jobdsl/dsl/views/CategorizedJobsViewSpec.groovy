package javaposse.jobdsl.dsl.views

class CategorizedJobsViewSpec extends ListViewSpec<CategorizedJobsView> {
    def setup() {
        view = new CategorizedJobsView(jobManagement, 'test')
    }

    def 'do nothing on empty categorization criteria'() {
        when:
        view.categorizationCriteria {
        }

        then:
        with(view.node) {
            categorizationCriteria.size() == 1
            categorizationCriteria[0].value().size() == 0
        }
    }

    def 'group by regex with naming'() {
        when:
        view.categorizationCriteria {
            regexGroupingRule('regex', 'naming')
        }

        then:
        with(view.node) {
            categorizationCriteria[0].children().size() == 1
            with(categorizationCriteria[0].'org.jenkinsci.plugins.categorizedview.GroupingRule'[0]) {
                children().size() == 2
                groupRegex[0].value() == 'regex'
                namingRule[0].value() == 'naming'
            }
        }
    }

    def 'group by regex without naming'() {
        when:
        view.categorizationCriteria {
            regexGroupingRule('regex')
        }

        then:
        with(view.node) {
            categorizationCriteria[0].children().size() == 1
            with(categorizationCriteria[0].'org.jenkinsci.plugins.categorizedview.GroupingRule'[0]) {
                children().size() == 2
                groupRegex[0].value() == 'regex'
                namingRule[0].value() == ''
            }
        }
    }

    def 'add more than one group'() {
        when:
        view.categorizationCriteria {
            regexGroupingRule('regex1', 'naming1')
            regexGroupingRule('regex2', 'naming2')
        }

        then:
        with(view.node) {
            categorizationCriteria[0].children().size() == 2
            with(categorizationCriteria[0].'org.jenkinsci.plugins.categorizedview.GroupingRule'[0]) {
                children().size() == 2
                groupRegex[0].value() == 'regex1'
                namingRule[0].value() == 'naming1'
            }
            with(categorizationCriteria[0].'org.jenkinsci.plugins.categorizedview.GroupingRule'[1]) {
                children().size() == 2
                groupRegex[0].value() == 'regex2'
                namingRule[0].value() == 'naming2'
            }
        }
    }

    def 'hide aggregated info on categories'() {
        when:
        view.hideAggregatedInfoOnCategories()

        then:
        view.node.hideAggregatedInfoOnCategories[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '246.ve02ea_a_a_d21a_7')
    }

    def 'show aggregated info on categories'() {
        when:
        view.hideAggregatedInfoOnCategories(false)

        then:
        view.node.hideAggregatedInfoOnCategories[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '246.ve02ea_a_a_d21a_7')
    }

    def 'nested groups'() {
        when:
        view.nestedGroups()

        then:
        view.node.nestedGroups[0].value() == true
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '246.ve02ea_a_a_d21a_7')
    }

    def 'flat groups'() {
        when:
        view.nestedGroups(false)

        then:
        view.node.nestedGroups[0].value() == false
        1 * jobManagement.requireMinimumPluginVersion('categorized-view', '246.ve02ea_a_a_d21a_7')
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
}
