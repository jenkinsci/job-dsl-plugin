package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class FlowdockPublisherContext implements Context {
    List<String> notificationTags = []
    boolean chat = false

    boolean success = true
    boolean failure = true
    boolean fixed = true
    boolean unstable = false
    boolean aborted = false
    boolean notBuilt = false

    /**
     * Adds a tag that should be automatically added to the notification message. Can be called multiple times to add
     * more tags.
     */
    void tag(String tag) {
        Preconditions.checkArgument(tag != null && tag.length() > 0, 'A non-empty tag is required!')
        this.notificationTags.addAll(tag.tokenize(','))
    }

    /**
     * Adds tags that should be automatically added to the notification message. Can be called multiple times to add
     * more tags.
     */
    void tags(String[] tags) {
        tags.each { tag(it) }
    }

    /**
     * Sends a chat notification when the build fails. Defaults to {@code false}.
     */
    void chat(boolean chat = true) {
        this.chat = chat
    }

    /**
     * Sends a notification when the build is successful. Defaults to {@code true}.
     */
    void success(boolean success = true) {
        this.success = success
    }

    /**
     * Sends a notification when the build failed. Defaults to {@code true}.
     */
    void failure(boolean failure = true) {
        this.failure = failure
    }

    /**
     * Sends a notification when the build has been fixed. Defaults to {@code true}.
     */
    void fixed(boolean fixed = true) {
        this.fixed = fixed
    }

    /**
     * Sends a notification when the build is unstable. Defaults to {@code false}.
     */
    void unstable(boolean unstable = true) {
        this.unstable = unstable
    }

    /**
     * Sends a notification when the build has been aborted. Defaults to {@code false}.
     */
    void aborted(boolean aborted = true) {
        this.aborted = aborted
    }

    /**
     * Sends a notification when the build has not run. Defaults to {@code false}.
     */
    void notBuilt(boolean notBuilt = true) {
        this.notBuilt = notBuilt
    }
}
