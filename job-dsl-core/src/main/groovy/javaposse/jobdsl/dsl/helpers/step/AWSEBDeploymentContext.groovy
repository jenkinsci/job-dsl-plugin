package javaposse.jobdsl.dsl.helpers.step

import javaposse.jobdsl.dsl.AbstractContext
import javaposse.jobdsl.dsl.JobManagement

class AWSEBDeploymentContext extends AbstractContext {
    String credentialId
    String regionId
    String applicationName
    String environmentName
    String bucketName
    String keyPrefix
    String versionLabelFormat
    String rootObject
    String includes
    String excludes
    Boolean zeroDowntime = true
    Boolean checkHealth = true

    AWSEBDeploymentContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Specifies the credential to use.
     */
    void credentialId(String credentialId) {
        this.credentialId = credentialId
    }

    /**
     * Specifies the region to use.
     */
    void regionId(String regionId) {
        this.regionId = regionId
    }

    /**
     * Specifies the application name.
     */
    void applicationName(String applicationName) {
        this.applicationName = applicationName
    }

    /**
     * Specifies the environment name.
     */
    void environmentName(String environmentName) {
        this.environmentName = environmentName
    }

    /**
     * Specifies the S3 bucket name.
     */
    void bucketName(String bucketName) {
        this.bucketName = bucketName
    }

    /**
     * Specifies the S3 bucket key prefix.
     */
    void keyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix
    }

    /**
     * Specifies the version format to use.
     */
    void versionLabelFormat(String versionLabelFormat) {
        this.versionLabelFormat = versionLabelFormat
    }

    /**
     * Specifies the root object of the archive.
     */
    void rootObject(String rootObject) {
        this.rootObject = rootObject
    }

    /**
     * Specifies includes for root object if root object is a directory.
     */
    void includes(String includes) {
        this.includes = includes
    }

    /**
     * Specifies excludes for root object if root object is a directory.
     */
    void excludes(String excludes) {
        this.excludes = excludes
    }

    /**
     * Specifies whether to deploy with zero downtime.
     */
    void zeroDowntime(Boolean zeroDowntime = true) {
        this.zeroDowntime = zeroDowntime
    }

    /**
     * Specifies whether to ensure a healthy app for job to be a success.
     */
    void checkHealth(Boolean checkHealth = true) {
        this.checkHealth = checkHealth
    }

}
