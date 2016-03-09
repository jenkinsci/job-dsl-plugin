package javaposse.jobdsl.dsl.views.jobfilter

enum AmountType {
    HOURS('Hours'), DAYS('Days'), BUILDS('Builds')

    final String value

    AmountType(String value) {
        this.value = value
    }
}
