package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.Job

class RemoteContext implements Context {
    private final Item item

    String name
    String url
    String credentials
    String refspec
    Node browser

    RemoteContext(Item item) {
        this.item = item
    }

    /**
     * Sets a name for the remote.
     */
    void name(String name) {
        this.name = name
    }

    /**
     * Sets the remote URL.
     */
    void url(String url) {
        this.url = url
    }

    /**
     * Sets credentials for authentication with the remote repository.
     */
    void credentials(String credentials) {
        this.credentials = credentials
    }

    /**
     * Sets a refspec for the remote.
     */
    void refspec(String refspec) {
        this.refspec = refspec
    }

    /**
     * Sets a remote URL for a GitHub repository.
     *
     * The URL will be derived from the {@code ownerAndProject}, {@code protocol} and {@code host} parameters.
     * Supported protocols are {@code 'https'}, {@code 'ssh'} and {@code 'git'}.
     */
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
        if (item instanceof Job) {
            ((Job) item).properties {
                githubProjectUrl(webUrl)
            }
        }
    }
}
