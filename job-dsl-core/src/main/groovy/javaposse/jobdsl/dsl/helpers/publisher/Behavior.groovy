package javaposse.jobdsl.dsl.helpers.publisher

enum Behavior {
    DoNothing(0),
    MarkUnstable(1),
    MarkFailed(2)

    final int value
    Behavior(int value) {
        this.value = value
    }
}