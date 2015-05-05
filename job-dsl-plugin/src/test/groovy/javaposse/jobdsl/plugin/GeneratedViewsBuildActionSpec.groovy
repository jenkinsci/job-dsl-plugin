package javaposse.jobdsl.plugin

import javaposse.jobdsl.dsl.GeneratedView
import spock.lang.Specification

class GeneratedViewsBuildActionSpec extends Specification {
    Collection<GeneratedView> modifiedViews = [Mock(GeneratedView), Mock(GeneratedView)]

    def 'interface methods'() {
        when:
        GeneratedViewsBuildAction action = new GeneratedViewsBuildAction(modifiedViews, LookupStrategy.JENKINS_ROOT)

        then:
        action.iconFileName == null
        action.displayName == 'Generated Views'
        action.urlName == 'generatedViews'
        action.modifiedObjects != null
        action.modifiedObjects.size() == modifiedViews.size()
        action.modifiedObjects.containsAll(modifiedViews)
    }
}
