job('example') {
    publishers {
        mantis {
            keepNotePrivate()
            recordChangelogToNote()
        }
    }
}
