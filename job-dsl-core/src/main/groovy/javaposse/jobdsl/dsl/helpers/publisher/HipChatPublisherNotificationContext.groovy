package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class HipChatPublisherNotificationContext implements Context {
    boolean notifyEnabled
    boolean textFormat = false
    String messageTemplate

    /**
     * Sends a notification when the build event is triggered. Defaults to {@code true}
     * for failure events.
     */
    void notifyEnabled(boolean notifyEnabled = true) {
        this.notifyEnabled = notifyEnabled
    }

    /**
     * Sends the notification in text format. Defaults to {@code false}.
     */
    void textFormat(boolean textFormat = true) {
        this.textFormat = textFormat
    }

    /**
     * Configures the message that will be displayed in the room.
     */
    void messageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate
    }
}
