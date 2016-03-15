package javaposse.jobdsl.dsl.views.jobfilter

enum BuildCountType {
    LATEST('Latest'), AT_LEAST_ONE('AtLeastOne'), ALL('All')

    final String value

    BuildCountType(String value) {
        this.value = value
    }
}
