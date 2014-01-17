package javaposse.jobdsl.dsl.helpers.wrapper

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context


@Canonical
class PortsContext implements Context {
    def simplePorts = []
    def glassfishPorts = []
    def tomcatPorts = []

    def port(String port, String... ports) {
        simplePorts << new Port(port: port)
        ports.each {
            simplePorts << new Port(port: port)
        }
    }

    def glassfish(String port, String user, String password) {
        glassfishPorts << new Port(port: port, username: user, password: password)
    }

    def tomcat(String port, String password) {
        tomcatPorts << new Port(port: port, password: password)
    }

    static class Port {
        String port
        String username
        String password
    }
}
