package javaposse.jobdsl.dsl.views.jobfilter

class RegexFilter extends AbstractJobFilter {
    RegexMatchValue matchValue = RegexMatchValue.NAME
    String regex

    void matchValue(RegexMatchValue value) {
        this.matchValue = value
    }

    void regex(String regex) {
        this.regex = regex
    }
}
