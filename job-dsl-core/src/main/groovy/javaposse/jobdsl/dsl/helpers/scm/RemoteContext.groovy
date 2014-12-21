package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.WithXmlAction

class RemoteContext implements Context {
    private final List<WithXmlAction> withXmlActions

    String name
    String url
    String credentials
    String refspec
    Node browser

    RemoteContext(List<WithXmlAction> withXmlActions) {
        this.withXmlActions = withXmlActions
    }

    void name(String name) {
        this.name = name
    }

    void url(String url) {
        this.url = url
    }

    void credentials(String credentials) {
        this.credentials = credentials
    }

    void refspec(String refspec) {
        this.refspec = refspec
    }

    void github(String ownerAndProject, String protocol = 'https', String host = 'github.com') {
        switch (protocol) {
            case 'https':
                url = "https://${host}/${ownerAndProject}.git"
                break
            case 'ssh':
                url = "git@${host}:${ownerAndProject}.git"
                break
            case 'git':
                url = "git://${host}/${ownerAndProject}.git"
                break
            default:
                throw new IllegalArgumentException("Invalid protocol ${protocol}. Only https, ssh or git are allowed.")
        }
        String webUrl = "https://${host}/${ownerAndProject}/"
        browser = NodeBuilder.newInstance().browser(class: 'hudson.plugins.git.browser.GithubWeb') {
            delegate.url(webUrl)
        }
        withXmlActions << WithXmlAction.create {
            it / 'properties' / 'com.coravy.hudson.plugins.github.GithubProjectProperty' {
                projectUrl webUrl
            }
        }
    }
}
