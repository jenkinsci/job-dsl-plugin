package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions

import javaposse.jobdsl.dsl.helpers.Context

class EmmaContext implements Context {

    IntRange classRange = 0..100
    IntRange methodRange = 0..70
    IntRange blockRange = 0..80
    IntRange lineRange = 0..80
    IntRange conditionRange = 0..<1

    void minClass(int min) {
        classThreshold(min..Math.max(this.classRange.to, min))
    }

    void maxClass(int max) {
        classThreshold(this.classRange.from..max)
    }

    void classThreshold(IntRange range) {
        checkRange('class', range)

        this.classRange = range
    }

    void minMethod(int min) {
        methodThreshold(min..Math.max(this.methodRange.to, min))
    }

    void maxMethod(int max) {
        methodThreshold(this.methodRange.from..max)
    }

    void methodThreshold(IntRange range) {
        checkRange('method', range)

        this.methodRange = range
    }

    void minBlock(int min) {
        blockThreshold(min..Math.max(this.blockRange.to, min))
    }

    void maxBlock(int max) {
        blockThreshold(this.blockRange.from..max)
    }

    void blockThreshold(IntRange range) {
        checkRange('block', range)

        this.blockRange = range
    }

    void minLine(int min) {
        lineThreshold(min..Math.max(this.lineRange.to, min))
    }

    void maxLine(int max) {
        lineThreshold(this.lineRange.from..max)
    }

    void lineThreshold(IntRange range) {
        checkRange('line', range)

        this.lineRange = range
    }

    void minCondition(int min) {
        conditionThreshold(min..Math.max(this.conditionRange.to, min))
    }

    void maxCondition(int max) {
        conditionThreshold(this.conditionRange.from..max)
    }

    void conditionThreshold(IntRange range) {
        checkRange('condition', range)

        this.conditionRange = range
    }

    private void checkRange(String type, IntRange range) {
        Preconditions.checkArgument((0..100).contains(range.getFrom()), "Invalid ${type} threshold minimum, percentage (0-100) expected")
        Preconditions.checkArgument((0..100).contains(range.getTo()), "Invalid ${type} threshold maximum, percentage (0-100) expected")
    }
}
