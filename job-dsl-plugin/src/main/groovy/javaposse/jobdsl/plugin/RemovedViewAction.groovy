package javaposse.jobdsl.plugin

enum RemovedViewAction {
    IGNORE('Ignore'),
    DELETE('Delete')

    final String displayName

    RemovedViewAction(String displayName) {
        this.displayName = displayName
    }
}
