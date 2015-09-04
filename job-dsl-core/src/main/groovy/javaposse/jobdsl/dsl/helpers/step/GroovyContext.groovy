package javaposse.jobdsl.dsl.helpers.step

class GroovyContext extends AbstractGroovyContext {
    List<String> groovyParams = []
    List<String> scriptParams = []
    List<String> props = []
    List<String> javaOpts = []
    String groovyInstallation = null

    /**
     * Adds a parameter for the Groovy executable. Can be called multiple times to add more parameters.
     */
    void groovyParam(String param) {
        groovyParams << param
    }

    /**
     * Adds parameters for the Groovy executable. Can be called multiple times to add more parameters.
     */
    void groovyParams(Iterable<String> params) {
        params.each { groovyParam(it) }
    }

    /**
     * Adds a parameter for the Groovy script. Can be called multiple times to add more parameters.
     */
    void scriptParam(String param) {
        scriptParams << param
    }

    /**
     * Adds parameters for the Groovy script. Can be called multiple times to add more parameters.
     */
    void scriptParams(Iterable<String> params) {
        params.each { scriptParam(it) }
    }

    /**
     * Adds a property (-D parameter) for the Groovy script. Can be called multiple times to add more parameters.
     */
    void prop(String key, String value) {
        props << "${key}=${value}"
    }

    /**
     * Adds properties (-D parameter) for the Groovy script. Can be called multiple times to add more parameters.
     */
    void props(Map<String, String> map) {
        map.entrySet().each {
            prop(it.key, it.value)
        }
    }

    /**
     * Adds a {@code JAVA_OPTS} option. Can be called multiple times to add more options.
     */
    void javaOpt(String opt) {
        javaOpts << opt
    }

    /**
     * Adds {@code JAVA_OPTS} options. Can be called multiple times to add more options.
     */
    void javaOpts(Iterable<String> opts) {
        opts.each { javaOpt(it) }
    }

    /**
     * Specifies the name of the Groovy installation to be used for executing the script.
     */
    void groovyInstallation(String groovyInstallationName) {
        groovyInstallation = groovyInstallationName
    }
}
