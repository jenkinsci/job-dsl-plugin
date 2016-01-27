job('example') {
    publishers {
        extendedEmail {
            recipientList('me@halfempty.org')
            defaultSubject('Oops')
            defaultContent('Something broken')
            contentType('text/html')
            triggers {
                beforeBuild()
                stillUnstable {
                    subject('Subject')
                    content('Body')
                    sendTo {
                        developers()
                        requester()
                        culprits()
                    }
                }
            }
        }
    }
}
