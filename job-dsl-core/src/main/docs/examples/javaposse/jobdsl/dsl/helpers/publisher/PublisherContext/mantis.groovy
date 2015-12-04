job('example') {
    publishers {
        mantis {
            keepNotePrivate(false)
            recordChangelogToNote()
        }
    }
}
