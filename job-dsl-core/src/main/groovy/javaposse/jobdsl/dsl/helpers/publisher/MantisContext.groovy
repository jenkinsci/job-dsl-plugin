package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class MantisContext implements Context {
    boolean keepNotePrivate = false
    boolean recordChangelogToNote = false

    /**
     * If set, it keeps note private. Defaults to {@code false}
     */
    void keepNotePrivate(boolean keepNotePrivate = true) {
        this.keepNotePrivate = keepNotePrivate
    }

    /**
     * If set, records changelog to note. Defaults to {@code false}.
     */
    void recordChangelogToNote(boolean recordChangelogToNote = true) {
        this.recordChangelogToNote = recordChangelogToNote
    }
}
