package javaposse.jobdsl.dsl

public enum JobType {
    Freeform('project'),
    Maven('maven2-moduleset'),
    Multijob('com.tikal.jenkins.plugins.multijob.MultiJobProject'),

    String elementName

    public JobType(String elementName) {
        this.elementName = elementName
    }

    public static find(String enumName) {
        values().find { it.name().toLowerCase() == enumName.toLowerCase() }
    }
}
