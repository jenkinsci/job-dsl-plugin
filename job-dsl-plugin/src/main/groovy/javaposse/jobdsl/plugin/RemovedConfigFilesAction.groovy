package javaposse.jobdsl.plugin

/**
 * @since 1.62
 */
enum RemovedConfigFilesAction {
    IGNORE('Ignore'),
    DELETE('Delete')

    final String displayName

    RemovedConfigFilesAction(String displayName) {
        this.displayName = displayName
    }
}
