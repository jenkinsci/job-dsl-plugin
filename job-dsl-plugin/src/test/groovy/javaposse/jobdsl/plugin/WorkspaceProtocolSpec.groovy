package javaposse.jobdsl.plugin

import spock.lang.*
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.FileJobManagement
import javaposse.jobdsl.dsl.ScriptRequest

class WorkspaceProtocolSpec  extends Specification {


    def 'load workspace url'() {
        when:
        URL url = new URL("workspace://JOB/dir/file.dsl")

        then:
        url.host == 'JOB'
        url.file == '/dir/file.dsl'
    }

    def 'reference workspace form dsl'() {
        def resourcesDir = new File("src/test/resources")
        JobManagement jm = new FileJobManagement(resourcesDir)
        URL url = new URL("workspace://JOB/dir/file.dsl")

        setup:
        ScriptRequest request = new ScriptRequest('caller.dsl', url);

        when:
        DslScriptLoader.runDslEngine(request, jm)

        then:
        thrown(IllegalStateException)
    }
}
