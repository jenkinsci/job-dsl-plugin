package javaposse.jobdsl.dsl.views.jobfilter

enum PermissionCheckType {
    MUST_MATCH_ALL('MustMatchAll'),
    AT_LEAST_ONE('AtLeastOne')

    final String value

    PermissionCheckType(String value) {
        this.value = value
    }
}
