package javaposse.jobdsl.plugin.actions

import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.plugin.LookupStrategy
import spock.lang.Specification

class GeneratedViewsBuildActionSpec extends Specification {
    Collection<GeneratedView> modifiedViews = [Mock(GeneratedView), Mock(GeneratedView)]

    def 'interface methods'() {
        when:
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction(modifiedViews, LookupStrategy.JENKINS_ROOT)

        then:
        action.iconFileName == null
        action.displayName == null
        action.urlName == null
        action.modifiedObjects != null
        action.modifiedObjects.size() == modifiedViews.size()
        action.modifiedObjects.containsAll(modifiedViews)
    }
}
