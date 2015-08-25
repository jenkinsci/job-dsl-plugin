package javaposse.jobdsl.dsl.views.jobfilter

class RegexFilter extends AbstractJobFilter {
    RegexMatchValue matchValue = RegexMatchValue.NAME
    String regex

    /**
     * Selects the value to be matched. Possible values are {@code RegexMatchValue.NAME},
     * {@code RegexMatchValue.DESCRIPTION}, {@code RegexMatchValue.SCM}, {@code RegexMatchValue.EMAIL},
     * {@code RegexMatchValue.MAVEN}, {@code RegexMatchValue.SCHEDULE} or {@code RegexMatchValue.NODE}.
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
