package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class TestStatisticsGridContext implements Context {
    String displayName = 'Test Statistics Grid'
    boolean useBackgroundColors = false
    String skippedColor = 'FDB813'
    String successColor = '71E66D'
    String failureColor = 'E86850'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Test Statistics Grid'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     * If set, displays a colored background. Defaults to {@code false}.
     */
    void useBackgroundColors(boolean useBackgroundColors = true) {
        this.useBackgroundColors = useBackgroundColors
    }

    /**
     * Sets the color for skipped tests as hex value. Defaults to {@code 'FDB813'}. Sets {@code useBackgroundColors} to
     * {@code true}.
     */
    void skippedColor(String skippedColor) {
        useBackgroundColors()
        this.skippedColor = skippedColor
    }

    /**
     * Sets the color for successful tests as hex value. Defaults to {@code '71E66D'}. Sets {@code useBackgroundColors}
     * to {@code true}.
     */
    void successColor(String successColor) {
        useBackgroundColors()
        this.successColor = successColor
    }

    /**
     * Sets the color for failed tests as hex value. Defaults to {@code 'E86850'}. Sets {@code useBackgroundColors} to
     * {@code true}.
     */
    void failureColor(String failureColor) {
        useBackgroundColors()
        this.failureColor = failureColor
    }
}
