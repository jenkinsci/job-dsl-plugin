package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.Context

class PortsContext implements Context {
    List<Port> simplePorts = []
    List<Port> glassfishPorts = []
    List<Port> tomcatPorts = []

    /**
     * Allocates plain TCP ports.
     */
    void port(String port, String... ports) {
        simplePorts << new Port(port: port)
        ports.each {
            simplePorts << new Port(port: port)
        }
    }

    /**
     * Allocates a GlassFish JMX port that lets Jenkins shut down a run-away GlassFish through JMX.
     *
     * For security reasons, do not use a hard-coded password. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void glassfish(String port, String user, String password) {
        glassfishPorts << new Port(port: port, username: user, password: password)
    }

    /**
     * Allocates a Tomcat shutdown port that lets Jenkins shut down a run-away Tomcat through the shut down port.
     *
     * For security reasons, do not use a hard-coded password. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void tomcat(String port, String password) {
        tomcatPorts << new Port(port: port, password: password)
    }

    static class Port {
        String port
        String username
        String password
    }
}
