package javaposse.jobdsl.dsl.helpers.publisher

import com.google.common.base.Preconditions
import javaposse.jobdsl.dsl.helpers.Context

class FlowdockPublisherContext implements Context {

    String[] notificationTags = []
    boolean chat = false

    boolean success = true
    boolean failure = true
    boolean fixed = true
    boolean unstable = false
    boolean aborted = false
    boolean notBuilt = false

    void tag(String tag) {
        Preconditions.checkArgument(tag != null && tag.length() > 0, "A non-empty tag is required!")
        this.notificationTags + tag.tokenize(',')
    }

    void tags(String[] tags) {
        tags.each { tag(it) }
    }

    void chat(boolean chat = true) {
        this.chat = chat
    }

    void success(boolean success = true) {
        this.success = success
    }

    void failure(boolean failure = true) {
        this.failure = failure
    }

    void fixed(boolean fixed = true) {
        this.fixed = fixed
    }

    void unstable(boolean unstable = true) {
        this.unstable = unstable
    }

    void aborted(boolean aborted = true) {
        this.aborted = aborted
    }

    void notBuilt(boolean notBuilt = true) {
        this.notBuilt = notBuilt
    }
}
