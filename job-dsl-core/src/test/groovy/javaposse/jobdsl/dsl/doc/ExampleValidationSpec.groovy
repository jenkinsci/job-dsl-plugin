package javaposse.jobdsl.dsl.doc

import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.ScriptRequest
import spock.lang.Specification
import spock.lang.Unroll

class ExampleValidationSpec extends Specification {
    private static final File EXAMPLES = new File('src/main/docs/examples')
    private static final String FILE_SEPARATOR = System.properties['file.separator']

    @Unroll
    def 'test examples in #file'() {
        setup:
        JobManagement jobManagement = Mock(JobManagement)
        jobManagement.parameters >> [:]
        jobManagement.outputStream >> System.out
        jobManagement.readFileInWorkspace(_) >> 'test'
        jobManagement.getConfig(_) >> '<project/>'
        jobManagement.getVSphereCloudHash(_) >> 1234
        jobManagement.getPermissions(_) >> [
                'hudson.model.Item.Create', 'hudson.model.Item.Discover',
                'hudson.model.Item.Workspace', 'hudson.model.Item.Build'
        ]

        when:
        new DslScriptLoader(jobManagement).runScripts([new ScriptRequest(file.text)])

        then:
        noExceptionThrown()

        where:
        file << findAllExamples()
    }

    @Unroll
    def 'file name matches method name in #file'() {
        when:
        String className = (file.parent - EXAMPLES.path).replace(FILE_SEPARATOR, '.')[1..-1]

        then:
        Class theClass = DslScriptLoader.classLoader.loadClass(className)

        when:
        String methodName = file.name[0..file.name.indexOf('.') - 1]

        then:
        theClass.methods.any { it.name == methodName }

        where:
        file << findAllExamples()
    }

    private static List<File> findAllExamples() {
        List<File> result = []
        EXAMPLES.eachFileRecurse(FileType.FILES) {
            result << it
        }
        result
    }
}
