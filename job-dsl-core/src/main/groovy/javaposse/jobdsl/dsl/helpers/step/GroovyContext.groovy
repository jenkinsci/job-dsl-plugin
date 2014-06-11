package javaposse.jobdsl.dsl.helpers.step

class GroovyContext extends AbstractGroovyContext {
    def groovyParams = []
    def scriptParams = []
    def props = []
    def javaOpts = []
    def groovyInstallation = null

    def groovyParam(String param) {
        groovyParams << param
    }

    def groovyParams(Iterable<String> params) {
        params.each { groovyParam(it) }
    }

    def scriptParam(String param) {
        scriptParams << param
    }

    def scriptParams(Iterable<String> params) {
        params.each { scriptParam(it) }
    }

    def prop(String key, String value) {
        props << "${key}=${value}"
    }

    def props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    def javaOpt(String opt) {
        javaOpts << opt
    }

    def javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    def groovyInstallation(String groovyInstallationName) {
        groovyInstallation = groovyInstallationName
    }
}
