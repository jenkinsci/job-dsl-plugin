package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class EmmaContext implements Context {

    IntRange classThreshold = 0..100
    IntRange methodThreshold = 0..70
    IntRange blockThreshold = 0..80
    IntRange lineThreshold = 0..80
    IntRange conditionThreshold = 0..<1

    void minClass(int min) {
        this.classThreshold.from = min
    }

    void maxClass(int max) {
        this.classThreshold.to = max
    }

    void 'class'(IntRange range)
    {
        this.classThreshold = range
    }

    void minMethod(int min) {
        this.methodThreshold.from = min
    }

    void maxMethod(int max) {
        this.methodThreshold.to = max
    }

    void method(IntRange range)
    {
        this.methodThreshold = range
    }

    void minBlock(int min) {
        this.blockThreshold.from = min
    }

    void maxBlock(int max) {
        this.blockThreshold.to = max
    }

    void block(IntRange range)
    {
        this.blockThreshold = range
    }

    void minLine(int min) {
        this.lineThreshold.from = min
    }

    void maxLine(int max) {
        this.lineThreshold.to = max
    }

    void line(IntRange range)
    {
        this.lineThreshold = range
    }

    void minCondition(int min) {
        this.conditionThreshold.from = min
    }

    void maxCondition(int max) {
        this.conditionThreshold.to = max
    }

    void condition(IntRange range)
    {
        this.conditionThreshold = range
    }
}