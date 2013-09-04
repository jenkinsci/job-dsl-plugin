package javaposse.jobdsl.dsl

public enum AxisType {
    Text('hudson.matrix.TextAxis'),
    Label('hudson.matrix.LabelAxis'),
    LabelExp('hudson.matrix.LabelExpAxis')

    String axisName

    public AxisType(String elementName) {
        this.axisName = elementName
    }
}
