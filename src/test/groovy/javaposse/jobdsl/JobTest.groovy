package javaposse.jobdsl

import spock.lang.*

class JobTest extends Specification {
    def "construct a job manually (not from a DSL script)"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)

        then:
        notThrown(Exception)
    }

    def "set name on a manually constructed job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)
        job.name = "NAME"

        then:
        job.name == "NAME"
    }

    def "load an empty template from a manually constructed job"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)

        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> '''
            <project>
                <actions/>
                <description></description>
                <keepDependencies>false</keepDependencies>
                <properties/>
            </project>
        '''
    }

    def "load an empty template from a manually constructed job and generate xml from it"() {
        setup:
        JobManagement jm = Mock()
        Job job = new Job(jm)
        def xml = '''
            <project>
                <actions/>
                <description></description>
                <keepDependencies>false</keepDependencies>
                <properties/>
            </project>
        '''

        when:
        job.using("TMPL")

        then:
        1 * jm.getConfig("TMPL") >> xml
        job.xml == '<?xml version="1.0" encoding="UTF-8"?>' + xml
    }

//    def "generate job from missing template"() {
//        setup:
//        JobManagement jm = Mock()
//        Job job = new Job(jm)
//        def xml = '''
//            <project>
//                <actions/>
//                <description></description>
//                <keepDependencies>false</keepDependencies>
//                <properties/>
//            </project>
//        '''
//        jm.getConfig(("TMPL")) >> xml
//        job.xml == '<?xml version="1.0" encoding="UTF-8"?>' + xml
//
//        when:
//        job.using("TMPL-NOT_THERE")
//
//        then:
//        // failure expected
//    }
}
