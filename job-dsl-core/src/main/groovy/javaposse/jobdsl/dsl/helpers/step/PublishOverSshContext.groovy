package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static com.google.common.base.Preconditions.checkArgument

class PublishOverSshContext implements Context {
    final List<PublishOverSshServerContext> servers = []
    boolean continueOnError
    boolean failOnError
    boolean alwaysPublishFromMaster
    String parameterName

    void continueOnError(boolean continueOnError = true) {
        this.continueOnError = continueOnError
    }

    void failOnError(boolean failOnError = true) {
        this.failOnError = failOnError
    }

    void alwaysPublishFromMaster(boolean alwaysPublishFromMaster = true) {
        this.alwaysPublishFromMaster = alwaysPublishFromMaster
    }

    void parameterizedPublishing(String parameterName) {
        this.parameterName = parameterName
    }

    void server(String name, @DslContext(PublishOverSshServerContext) Closure serverClosure) {
        PublishOverSshServerContext serverContext = new PublishOverSshServerContext(name)
        ContextHelper.executeInContext(serverClosure, serverContext)

        checkArgument(!serverContext.transferSets.empty, "At least 1 transferSet must be configured for ${name}")

        servers << serverContext
    }

    Node getNode() {
        new NodeBuilder().'delegate' {
            publishers {
                servers.each { server ->
                    'jenkins.plugins.publish__over__ssh.BapSshPublisher' {
                        configName(server.name)
                        verbose(server.verbose)
                        transfers {
                            server.transferSets.each { transferSet ->
                                'jenkins.plugins.publish__over__ssh.BapSshTransfer' {
                                    remoteDirectory(transferSet.remoteDirectory ?: '')
                                    sourceFiles(transferSet.sourceFiles ?: '')
                                    excludes(transferSet.excludeFiles ?: '')
                                    removePrefix(transferSet.removePrefix ?: '')
                                    remoteDirectorySDF(transferSet.remoteDirIsDateFormat)
                                    flatten(transferSet.flattenFiles)
                                    cleanRemote(false)
                                    noDefaultExcludes(transferSet.noDefaultExcludes)
                                    makeEmptyDirs(transferSet.makeEmptyDirs)
                                    patternSeparator(transferSet.patternSeparator)
                                    execCommand(transferSet.execCommand ?: '')
                                    execTimeout(transferSet.execTimeout)
                                    usePty(transferSet.execInPty)
                                }
                            }
                        }
                        useWorkspaceInPromotion(false)
                        usePromotionTimestamp(false)
                        if (server.retry) {
                            retry(class: 'jenkins.plugins.publish_over_ssh.BapSshRetry') {
                                retries(server.retries)
                                retryDelay(server.delay)
                            }
                        }
                        if (server.credentials) {
                            credentials(class: 'jenkins.plugins.publish_over_ssh.BapSshCredentials') {
                                secretPassphrase('')
                                key(server.credentials.key ?: '')
                                keyPath(server.credentials.pathToKey ?: '')
                                username(server.credentials.username)
                            }
                        }
                        if (server.label) {
                            label(class: 'jenkins.plugins.publish_over_ssh.BapSshPublisherLabel') {
                                label(server.label)
                            }
                        }
                    }
                }
            }
            delegate.continueOnError(continueOnError)
            delegate.failOnError(failOnError)
            delegate.alwaysPublishFromMaster(alwaysPublishFromMaster)
            hostConfigurationAccess(
                    class: 'jenkins.plugins.publish_over_ssh.BapSshPublisherPlugin',
                    reference: '../..',
            )
            if (parameterName) {
                paramPublish(class: 'jenkins.plugins.publish_over_ssh.BapSshParamPublish') {
                    delegate.parameterName(parameterName)
                }
            }
        }
    }
}
