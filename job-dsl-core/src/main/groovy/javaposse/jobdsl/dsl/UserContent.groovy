package javaposse.jobdsl.dsl

class UserContent {
    final String path
    final InputStream content

    UserContent(String path, InputStream content) {
        this.path = path
        this.content = content
    }
}
