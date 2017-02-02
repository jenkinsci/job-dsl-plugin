package javaposse.jobdsl.dsl

import spock.lang.Specification

class ScriptRequestSpec extends Specification {
    def 'script name'() {
        expect:
        new ScriptRequest('', [] as URL[], false, 'foo\\bar.txt').scriptName == 'bar.txt'
        new ScriptRequest('', [] as URL[], false, 'foo/bar.txt').scriptName == 'bar.txt'
        new ScriptRequest('', '', [] as URL[], false, 'foo/bar.txt').scriptName == 'bar.txt'
        new ScriptRequest('', '', [] as URL[], false, '').scriptName == null
        new ScriptRequest('foo.txt', '', [] as URL[]).scriptName == 'foo.txt'
        new ScriptRequest('script').scriptName == null
    }

    def 'script base name'() {
        expect:
        new ScriptRequest('', [] as URL[], false, 'foo\\bar.txt').scriptBaseName == 'bar'
        new ScriptRequest('', [] as URL[], false, 'foo/bar.txt').scriptBaseName == 'bar'
        new ScriptRequest('', '', [] as URL[], false, 'foo/bar.txt').scriptBaseName == 'bar'
        new ScriptRequest('', '', [] as URL[], false, '').scriptBaseName == null
        new ScriptRequest('foo.txt', '', [] as URL[]).scriptBaseName == 'foo'
        new ScriptRequest('script').scriptBaseName == null
    }
}
