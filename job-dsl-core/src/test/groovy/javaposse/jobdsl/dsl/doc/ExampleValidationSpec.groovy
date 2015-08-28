package javaposse.jobdsl.dsl.doc

import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import spock.lang.Specification
import spock.lang.Unroll

class ExampleValidationSpec extends Specification {
    @Unroll
    def 'test examples in #file'() {
        setup:
        JobManagement jobManagement = Mock(JobManagement)
        jobManagement.parameters >> [:]
        jobManagement.outputStream >> System.out
        jobManagement.readFileInWorkspace(_) >> 'test'
        jobManagement.createOrUpdateConfigFile(*_) >> 'id'
        jobManagement.getConfig(_) >> '<project/>'
        jobManagement.getConfigFileId(*_) >> 'id'
        jobManagement.getVSphereCloudHash(_) >> 1234
        jobManagement.getCredentialsId(_) >> 'id'
        jobManagement.getPermissions(_) >> [
                'hudson.model.Item.Create', 'hudson.model.Item.Discover',
                'hudson.model.Item.Workspace', 'hudson.model.Item.Build'
        ]

        when:
        DslScriptLoader.runDslEngine(file.text, jobManagement)

        then:
        noExceptionThrown()

        where:
        file << findAllExamples()
    }

    private static List<File> findAllExamples() {
        List<File> result = []
        new File('src/main/docs/examples').eachFileRecurse(FileType.FILES) {
            result << it
        }
        result
    }
}
