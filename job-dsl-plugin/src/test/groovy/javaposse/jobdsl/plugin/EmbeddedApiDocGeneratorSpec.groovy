package javaposse.jobdsl.plugin

import net.sf.json.JSONObject
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule
import spock.lang.Specification

class EmbeddedApiDocGeneratorSpec extends Specification {
    @Rule
    JenkinsRule jenkinsRule = new JenkinsRule()

    def 'generate API'() {
        when:
        String api = new  EmbeddedApiDocGenerator().generateApi()

        then:
        api == JSONObject.fromObject(getClass().getResource('/expected-dsl.json').getText('UTF-8')).toString()
    }

    def 'extract first sentence'() {
        expect:
        EmbeddedApiDocGenerator.firstSentence('Foo. Bar.') == 'Foo.'
        EmbeddedApiDocGenerator.firstSentence('<!-- Foo. --> Bar.') == 'Bar.'
        EmbeddedApiDocGenerator.firstSentence('Lorem &amp; Ipsum') == 'Lorem & Ipsum'
        EmbeddedApiDocGenerator.firstSentence('<div>Lorem Ipsum</div>') == 'Lorem Ipsum'
        EmbeddedApiDocGenerator.firstSentence('  Lorem Ipsum   ') == 'Lorem Ipsum'
    }
}
