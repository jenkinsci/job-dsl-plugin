package javaposse.jobdsl

import spock.lang.*

class JobTest extends Specification {
    def "construct a job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)

        then:
        notThrown(Exception)
    }

    def "set name on a job"() {
        setup:
        JobManagement jm = Mock()

        when:
        def job = new Job(jm)
        job.name = "NAME"

        then:
        job.name == "NAME"
    }

    def "load template from job"() {
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

    def "load template and generate xml from job"() {
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
//        job.xml == '<?xml version="1.0" encoding="UTF-8"?>'+ xml
//
//        when:
//        job.using("TMPL-NOT_THERE")
//
//        then:
//        // failure expected
//    }

//    def "generate job - template name absent"() {
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
//        job.xml == '<?xml version="1.0" encoding="UTF-8"?>'+ xml
//
//        when:
//        job.using("")
//
//        then:
//        // failure expected
//    }

//    def "generate job - add a name this time"() {
//        setup:
//        JobManagement jm = Mock()
//        Job job = new Job(jm)
//        def templateXml = '''
//            <project>
//                <actions/>
//                <description></description>
//                <keepDependencies>false</keepDependencies>
//                <properties/>
//            </project>
//        '''
//        jm.getConfig(("TMPL")) >> templateXml
////        job.xml == '<?xml version="1.0" encoding="UTF-8"?>'+ templateXml
//        job.using("TMPL")
//
//        def newJobXml = '''
//            <project>
//                <name>
//                <actions/>
//                <description></description>
//                <keepDependencies>false</keepDependencies>
//                <properties/>
//            </project>
//        '''
//
//        when:
//        jm.createOrUpdateConfig("NEW-JOB", "")
//
//    }
}
