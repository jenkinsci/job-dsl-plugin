package javaposse.jobdsl.dsl

public enum AxisType {
    TEXT('hudson.matrix.TextAxis'),
    LABEL('hudson.matrix.LabelAxis'),
    LABELEXP('hudson.matrix.LabelExpAxis')

    String axisName

    public AxisType(String elementName) {
        this.axisName = elementName
    }
}
