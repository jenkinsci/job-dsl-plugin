package javaposse.jobdsl.dsl.helpers.step

class GroovyContext extends AbstractGroovyContext {
    List<String> groovyParams = []
    List<String> scriptParams = []
    List<String> props = []
    List<String> javaOpts = []
    String groovyInstallation = null

    void groovyParam(String param) {
        groovyParams << param
    }

    void groovyParams(Iterable<String> params) {
        params.each { groovyParam(it) }
    }

    void scriptParam(String param) {
        scriptParams << param
    }

    void scriptParams(Iterable<String> params) {
        params.each { scriptParam(it) }
    }

    void prop(String key, String value) {
        props << "${key}=${value}"
    }

    void props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    void javaOpt(String opt) {
        javaOpts << opt
    }

    void javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    void groovyInstallation(String groovyInstallationName) {
        groovyInstallation = groovyInstallationName
    }
}
