package javaposse.jobdsl

import spock.lang.*

class JobManagementTest extends Specification {
    def "get config by name"() {
        // TODO: implement me
        // Should get a job as expected
    }

    def "get non-existent config"() {
        // TODO: implement me
        // Should throw a new "JobConfigurationNotFoundException (or something like this)
    }

    def "get config - no name provided"() {
        // TODO: implement me
        // Should return an empty, default config
    }

    def "create new config"() {
        // TODO: implement me
        // Should create a new job as expected
    }

    def "create new config - a config with the given name already exists"() {
        // TODO: implement me
        // Should throw a "JobNameClashException (or something like this)
    }

    def "create new config - name not provided"() {
        // TODO: implement me
        // Should throw a "JobNameMissingException (or something like this)
    }
}