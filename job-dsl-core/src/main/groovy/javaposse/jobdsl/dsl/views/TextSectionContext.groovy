package javaposse.jobdsl.dsl.views

import javaposse.jobdsl.dsl.JobManagement

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class TextSectionContext extends SectionContext {
    private static final List<String> VALID_STYLES = ['NONE', 'NOTE', 'INFO', 'WARNING', 'TIP']

    String style = 'NONE'
    String text

    TextSectionContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Sets the style of the section. Either {@code 'NONE'}, {@code 'NOTE'}, {@code 'INFO'}, {@code WARNING'}, or
     * {@code 'TIP'}.
     */
    void style(String style) {
        checkArgument(VALID_STYLES.contains(style), "style must be one of ${VALID_STYLES.join(', ')}")
        this.style = style
    }

    /**
     * Sets the text of the section.
     */
    void text(String text) {
        this.text = text
    }
}
