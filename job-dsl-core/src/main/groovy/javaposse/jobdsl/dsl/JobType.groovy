package javaposse.jobdsl.dsl

enum JobType {
    Freeform('project'),
    Maven('maven2-moduleset'),
    Multijob('com.tikal.jenkins.plugins.multijob.MultiJobProject'),
    BuildFlow('com.cloudbees.plugins.flow.BuildFlow'),
    Matrix('matrix-project')

    String elementName

    JobType(String elementName) {
        this.elementName = elementName
    }

    static find(String enumName) {
        values().find { it.name().toLowerCase() == enumName.toLowerCase() }
    }
}
