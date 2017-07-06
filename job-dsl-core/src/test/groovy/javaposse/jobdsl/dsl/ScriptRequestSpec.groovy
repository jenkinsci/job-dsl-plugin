package javaposse.jobdsl.dsl

import spock.lang.Specification

class ScriptRequestSpec extends Specification {
    def 'script name'() {
        expect:
        new ScriptRequest('', [] as URL[], false, 'foo\\bar.txt').scriptName == 'bar.txt'
        new ScriptRequest('', [] as URL[], false, 'foo/bar.txt').scriptName == 'bar.txt'
        new ScriptRequest('script').scriptName == null
    }

    def 'script base name'() {
        expect:
        new ScriptRequest('', [] as URL[], false, 'foo\\bar.txt').scriptBaseName == 'bar'
        new ScriptRequest('', [] as URL[], false, 'foo/bar.txt').scriptBaseName == 'bar'
        new ScriptRequest('script').scriptBaseName == null
    }
}
