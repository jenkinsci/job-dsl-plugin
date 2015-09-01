package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class EmmaContext implements Context {
    IntRange classRange = 0..100
    IntRange methodRange = 0..70
    IntRange blockRange = 0..80
    IntRange lineRange = 0..80
    IntRange conditionRange = 0..80

    /**
     * Reports health as 0% if class coverage is less than specified. Defaults to {@code 0}.
     */
    void minClass(int min) {
        classThreshold(min..Math.max(this.classRange.to, min))
    }

    /**
     * Reports health as 100% if class coverage is greater than specified. Defaults to {@code 100}.
     */
    void maxClass(int max) {
        classThreshold(Math.min(this.classRange.from, max)..max)
    }

    /**
     * Sets the thresholds for class coverage. Defaults to {@code 0..100}.
     */
    void classThreshold(IntRange range) {
        checkRange('class', range)

        this.classRange = range
    }

    /**
     * Reports health as 0% if method coverage is less than specified. Defaults to {@code 0}.
     */
    void minMethod(int min) {
        methodThreshold(min..Math.max(this.methodRange.to, min))
    }

    /**
     * Reports health as 100% if method coverage is greater than specified. Defaults to {@code 70}.
     */
    void maxMethod(int max) {
        methodThreshold(Math.min(this.methodRange.from, max)..max)
    }

    /**
     * Sets the thresholds for method coverage. Defaults to {@code 0..70}.
     */
    void methodThreshold(IntRange range) {
        checkRange('method', range)

        this.methodRange = range
    }

    /**
     * Reports health as 0% if block coverage is less than specified. Defaults to {@code 0}.
     */
    void minBlock(int min) {
        blockThreshold(min..Math.max(this.blockRange.to, min))
    }

    /**
     * Reports health as 100% if block coverage is greater than specified. Defaults to {@code 80}.
     */
    void maxBlock(int max) {
        blockThreshold(Math.min(this.blockRange.from, max)..max)
    }

    /**
     * Sets the thresholds for block coverage. Defaults to {@code 0..80}.
     */
    void blockThreshold(IntRange range) {
        checkRange('block', range)

        this.blockRange = range
    }

    /**
     * Reports health as 0% if line coverage is less than specified. Defaults to {@code 0}.
     */
    void minLine(int min) {
        lineThreshold(min..Math.max(this.lineRange.to, min))
    }

    /**
     * Reports health as 100% if line coverage is greater than specified. Defaults to {@code 80}.
     */
    void maxLine(int max) {
        lineThreshold(Math.min(this.lineRange.from, max)..max)
    }

    /**
     * Sets the thresholds for line coverage. Defaults to {@code 0..80}.
     */
    void lineThreshold(IntRange range) {
        checkRange('line', range)

        this.lineRange = range
    }

    /**
     * Reports health as 0% if branch coverage is less than specified. Defaults to {@code 0}.
     */
    void minCondition(int min) {
        conditionThreshold(min..Math.max(this.conditionRange.to, min))
    }

    /**
     * Reports health as 100% if branch coverage is greater than specified. Defaults to {@code 80}.
     */
    void maxCondition(int max) {
        conditionThreshold(Math.min(this.conditionRange.from, max)..max)
    }

    /**
     * Sets the thresholds for branch coverage. Defaults to {@code 0..80}.
     */
    void conditionThreshold(IntRange range) {
        checkRange('condition', range)

        this.conditionRange = range
    }

    private void checkRange(String type, IntRange range) {
        checkArgument((0..100).contains(range.from), "Invalid ${type} threshold minimum, percentage (0-100) expected")
        checkArgument((0..100).contains(range.to), "Invalid ${type} threshold maximum, percentage (0-100) expected")
    }
}
