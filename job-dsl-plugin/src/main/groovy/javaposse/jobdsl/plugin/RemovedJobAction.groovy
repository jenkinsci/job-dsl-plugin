package javaposse.jobdsl.plugin

enum RemovedJobAction {
    IGNORE('Ignore'),
    DISABLE('Disable'),
    DELETE('Delete'),
    SHELVE('Shelve')

    final String displayName

    RemovedJobAction(String displayName) {
        this.displayName = displayName
    }
}
