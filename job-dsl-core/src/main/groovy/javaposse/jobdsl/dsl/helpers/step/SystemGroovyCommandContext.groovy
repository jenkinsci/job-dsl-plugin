package javaposse.jobdsl.dsl.helpers.step

class SystemGroovyCommandContext extends SystemGroovyContext {
    boolean sandbox = false
    String script = ""

    /**
     * Sandbox mode
     */
    void sandbox(boolean value=true) {
        this.sandbox = value
    }

    /**
     * Script to be executed
     */
    void script(String value) {
        this.script = value
    }

}
