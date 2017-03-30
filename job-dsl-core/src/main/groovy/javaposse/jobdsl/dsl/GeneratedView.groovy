package javaposse.jobdsl.dsl

class GeneratedView implements Comparable<GeneratedView> {
    final String name
    final View view

    GeneratedView(View view) {
        if (! view) {
            throw new IllegalArgumentException('view cannot be null')
        }
        this.name = view.name
        this.view = view
    }

    @Deprecated
    GeneratedView(String name) {
        if (name == null) {
            throw new IllegalArgumentException()
        }
        this.name = name
    }

    @Override
    int hashCode() {
        name.hashCode()
    }

    @Override
    boolean equals(Object o) {
        if (this.is(o)) {
            return true
        }
        if (!(o instanceof GeneratedView)) {
            return false
        }

        GeneratedView that = (GeneratedView) o
        name == that.name
    }

    @Override
    String toString() {
        "GeneratedView{name='${name}'}"
    }

    @Override
    int compareTo(GeneratedView o) {
        name <=> o.name
    }
}
