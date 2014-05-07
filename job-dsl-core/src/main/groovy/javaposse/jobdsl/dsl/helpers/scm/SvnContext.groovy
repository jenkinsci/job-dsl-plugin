package javaposse.jobdsl.dsl.helpers.scm

import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.Context

import static javaposse.jobdsl.dsl.helpers.AbstractContextHelper.executeInContext

class SvnContext implements Context {
    
    Closure configureBlock
    String excludedRegions = ''
    String includedRegions = ''
    String excludedUsers = ''
    String excludedRevprop = ''
    String excludedCommitMessages = ''
    Updater updater = Updater.Update
    boolean ignoreExternals = false
    String svnUrl
    String localDir = '.'
    
    public enum Updater {
        /** Use 'svn update' as much as possible */
        Update, 
        /** Always check out a fresh copy */
        Checkout, 
        /** Emulate clean checkout by first deleting unversioned/ignored files, then 'svn update' */
        UpdateWithClean, 
        /** Use 'svn update' as much as possible, with 'svn revert' before update */
        UpdateWithRevert
    }
    
    void excludedRegions(String excludedRegions) {
        this.excludedRegions = excludedRegions
    }

    void includedRegions(String includedRegions) {
        this.includedRegions = includedRegions
    } 
    
    void excludedUsers(String excludedUsers) {
        this.excludedUsers = excludedUsers
    }
    
    void excludedRevprop(String excludedRevprop) {
        this.excludedRevprop = excludedRevprop
    }
    
    void excludedCommitMessages(String excludedCommitMessages) {
        this.excludedCommitMessages = excludedCommitMessages
    }
    
    void updateStrategie(String updater) {
        this.updater = Updater.valueOf(updater)
    }
    
    void ignoreExternals(boolean ignoreExternals = true) {
        this.ignoreExternals = ignoreExternals
    }
    
    void svnUrl(String svnUrl) {
        this.svnUrl = svnUrl
    }
    
    void localDir(String localDir) {
        this.localDir = localDir
    }
    
    void configure(Closure configureBlock) {
        this.configureBlock = configureBlock
    }
    
}
