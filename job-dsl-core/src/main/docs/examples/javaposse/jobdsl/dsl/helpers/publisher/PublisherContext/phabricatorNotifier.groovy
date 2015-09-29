job('example') {
    publishers {
        phabricatorNotifier {
            commentOnSuccess()
            enableUberalls()
            commentFile('.contributor-guide')
            preserveFormatting()
            commentSize(2000)
            commentWithConsoleLinkOnFailure()
        }
    }
}
