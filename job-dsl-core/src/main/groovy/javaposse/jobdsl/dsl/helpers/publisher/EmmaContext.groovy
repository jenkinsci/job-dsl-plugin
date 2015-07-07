package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class EmmaContext implements Context {
    IntRange classRange = 0..100
    IntRange methodRange = 0..70
    IntRange blockRange = 0..80
    IntRange lineRange = 0..80
    IntRange conditionRange = 0..80

    void minClass(int min) {
        classThreshold(min..Math.max(this.classRange.to, min))
    }

    void maxClass(int max) {
        classThreshold(Math.min(this.classRange.from, max)..max)
    }

    void classThreshold(IntRange range) {
        checkRange('class', range)

        this.classRange = range
    }

    void minMethod(int min) {
        methodThreshold(min..Math.max(this.methodRange.to, min))
    }

    void maxMethod(int max) {
        methodThreshold(Math.min(this.methodRange.from, max)..max)
    }

    void methodThreshold(IntRange range) {
        checkRange('method', range)

        this.methodRange = range
    }

    void minBlock(int min) {
        blockThreshold(min..Math.max(this.blockRange.to, min))
    }

    void maxBlock(int max) {
        blockThreshold(Math.min(this.blockRange.from, max)..max)
    }

    void blockThreshold(IntRange range) {
        checkRange('block', range)

        this.blockRange = range
    }

    void minLine(int min) {
        lineThreshold(min..Math.max(this.lineRange.to, min))
    }

    void maxLine(int max) {
        lineThreshold(Math.min(this.lineRange.from, max)..max)
    }

    void lineThreshold(IntRange range) {
        checkRange('line', range)

        this.lineRange = range
    }

    void minCondition(int min) {
        conditionThreshold(min..Math.max(this.conditionRange.to, min))
    }

    void maxCondition(int max) {
        conditionThreshold(Math.min(this.conditionRange.from, max)..max)
    }

    void conditionThreshold(IntRange range) {
        checkRange('condition', range)

        this.conditionRange = range
    }

    private void checkRange(String type, IntRange range) {
        checkArgument((0..100).contains(range.from), "Invalid ${type} threshold minimum, percentage (0-100) expected")
        checkArgument((0..100).contains(range.to), "Invalid ${type} threshold maximum, percentage (0-100) expected")
    }
}
