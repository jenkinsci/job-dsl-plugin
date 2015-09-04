package javaposse.jobdsl.dsl.views.jobfilter

class RegexFilter extends AbstractJobFilter {
    RegexMatchValue matchValue = RegexMatchValue.NAME
    String regex

    /**
     * Selects the value to be matched. Defaults to {@code RegexMatchValue.NAME}.
     */
    void matchValue(RegexMatchValue value) {
        this.matchValue = value
    }

    /**
     * Sets the regular expression used for filtering the selected value.
     */
    void regex(String regex) {
        this.regex = regex
    }
}
