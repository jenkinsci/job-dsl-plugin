package javaposse.jobdsl.dsl.views.jobfilter

class RegexFilter extends AbstractJobFilter {
    RegexMatchValue matchValue
    String regex

    final String className = 'hudson.views.RegExJobFilter'

    void matchValue(RegexMatchValue value) {
        this.matchValue = value
    }

    void regex(String regex) {
        this.regex = regex
    }

    protected void addArgs(NodeBuilder builder) {
        super.addArgs(builder)
        builder.valueTypeString(matchValue.name())
        builder.regex(regex)
    }
}
