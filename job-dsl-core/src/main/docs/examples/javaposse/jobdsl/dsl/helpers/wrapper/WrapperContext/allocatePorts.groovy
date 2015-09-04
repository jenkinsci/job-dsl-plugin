// allocates two ports: one randomly assigned and accessible by env var $HTTP
// the second is fixed and the port allocator controls concurrent usage
job('example-1') {
    wrappers {
        allocatePorts('HTTP', '8080')
    }
}

// allocates a GlassFish and a Tomcat port
job('example-2') {
    wrappers {
        allocatePorts {
            glassfish('1234', 'user', 'password')
            tomcat('1234', 'password')
        }
    }
}
