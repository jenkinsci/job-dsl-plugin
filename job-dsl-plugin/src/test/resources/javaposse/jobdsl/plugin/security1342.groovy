package javaposse.jobdsl.plugin

class Foo extends JenkinsJobParent {
    Foo() { new File("test") }

    @Override
    Object run() {
        return null
    }
}
