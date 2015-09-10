package javaposse.jobdsl.plugin

enum RemovedJobAction {
    IGNORE('Ignore'),
    DISABLE('Disable'),
    DELETE('Delete')

    final String displayName

    RemovedJobAction(String displayName) {
        this.displayName = displayName
    }
}
