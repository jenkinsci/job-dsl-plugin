package javaposse.jobdsl.dsl.helpers.workflow

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

/**
 * @since 1.56
 */
class BitbucketScmNavigatorContext extends AbstractContext {

  String repoOwner
  String scanCredentialsId
  String repositoryPattern = '.*'
  boolean autoRegisterWebhooks = false
  String checkoutCredentialsId = 'SAME'
  String bitbucketServerUrl
  int sshPort = -1

  BitbucketScmNavigatorContext(final JobManagement jobManagement) {
    super(jobManagement)
  }

  /**
   * Sets the name of the <b>Bitbucket Team</b> or <a>Bitbucket User Account</a>.
   * It could be a <a>Bitbucket Project</a> also, if using Bitbucket Server.
   *
   * In the case of Bitbucket Bitbucket Server, use the project key (not the name)
   * @param repoOwner the team name, user account, or project key
   * @since 1.56
   */
  void repoOwner(String repoOwner) {
    this.repoOwner = repoOwner
  }

  /**
   * Credentials used to scan branches and check out sources.
   * @param scanCredentialsId the credentials Id
   * @since 1.56
   */
  void scanCredentialsId(String scanCredentialsId) {
    this.scanCredentialsId = scanCredentialsId
  }

  /**
   * Activate this option to auto-register a hook on all discovered Bitbucket Cloud repositories.
   * @param autoRegisterWebhooks {@code true} to enable registration, {@code false} to disable
   * @since 1.56
   */
  void autoRegisterWebhooks(boolean autoRegisterWebhooks = true) {
    this.autoRegisterWebhooks = autoRegisterWebhooks
  }

  /**
   * Regular expression to specify what repositories one wants to include
   * @param repositoryNamePattern the regex pattern
   * @since 1.56
   */
  void repositoryPattern(String repositoryNamePattern) {
    this.repositoryPattern = repositoryNamePattern
  }

  /**
   * Credentials used to check out sources during a build.
   * If set, these are used in favor of the {@link #scanCredentialsId}.
   * @param checkoutCredentialsId the credentials Id
   * @since 1.56
   */
  void checkoutCredentialsId(String checkoutCredentialsId) {
    this.checkoutCredentialsId = checkoutCredentialsId
  }

  /**
   * Left blank to use Bitbucket Cloud. Set your Bitbucket Server base URL to use
   * your own server instance. The URL must contain the full URL
   * including a base path (if exists).
   * @param bitbucketServerUrl full path to Bitbucket Server instance
   * @since 1.56
   */
  void bitbucketServerUrl(String bitbucketServerUrl) {
    this.bitbucketServerUrl = bitbucketServerUrl
  }

  /**
   * Left blank to use Bitbucket Cloud. SSH Port as configured in Bitbucket Server settings.
   * @param sshPort the port to use
   * @since 1.56
   */
  void sshPort(int sshPort) {
    this.sshPort = sshPort
  }
}
