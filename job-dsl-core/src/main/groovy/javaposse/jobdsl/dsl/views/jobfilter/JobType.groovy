package javaposse.jobdsl.dsl.views.jobfilter

enum JobType {
    FREE_STYLE_PROJECT('hudson.model.FreeStyleProject'),
    MAVEN_MODULE_SET('hudson.maven.MavenModuleSet'),
    EXTERNAL_JOB('hudson.model.ExternalJob'),
    MATRIX_PROJECT('hudson.matrix.MatrixProject')

    final String value

    JobType(String value) {
        this.value = value
    }
}
