job('example') {
    publishers {
        mattermost {
            room('foobar team')
            notifySuccess()
            notifyAborted()
            notifyBackToNormal()
            showCommitList()
        }
    }
}

job('example2') {
    publishers {
        mattermost {
            endpoint('https://mattermost.my-company.de/hooks/um9hcztr1jfnzgph6397pcoeur')
            icon('http://jenkins.my-company.de/static/3d886fd0/images/foobar.png')
            room('foobar team')
            notifySuccess()
            notifyAborted()
            notifyBackToNormal()
            showCommitList()
        }
    }
}
