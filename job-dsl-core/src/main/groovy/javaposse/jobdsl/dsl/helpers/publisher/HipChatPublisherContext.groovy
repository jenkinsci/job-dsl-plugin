package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.ContextHelper
import javaposse.jobdsl.dsl.DslContext

import static javaposse.jobdsl.dsl.Preconditions.checkNotNull

class HipChatPublisherContext implements Context {

    List<Node> notificationNodes = []

    String token
    List<String> rooms = []
    String startJobMessage
    String completeJobMessage

    /**
     * Sets either a v1 admin/notification API token, or a v2 access token with send_notification scope.
     *
     * For security reasons, do not use a hard-coded token. See
     * <a href="https://github.com/jenkinsci/job-dsl-plugin/wiki/Handling-Credentials">Handling Credentials</a> for
     * details about handling credentials in DSL scripts.
     */
    void token(String token) {
        this.token = token
    }

    /**
     * Specifies the room names to which notifications should be sent.
     */
    void rooms(String... rooms) {
        this.rooms.addAll(rooms)
    }

    /**
     * Configures the message that will be displayed in the room when the build starts.
     */
    void startJobMessage(String startJobMessage) {
        this.startJobMessage = startJobMessage
    }

    /**
     * Configures the message that will be displayed in the room when the build is completed.
     */
    void completeJobMessage(String completeJobMessage) {
        this.completeJobMessage = completeJobMessage
    }

    /**
     * Adds a notification. Can be called multiple times to add more notifications.
     */
    void notification(NotificationType notificationType, NotificationColor notificationColor,
                      @DslContext(HipChatPublisherNotificationContext) Closure notifyClosure) {
        checkNotNull(notificationType, 'Notification type must be specified')
        checkNotNull(notificationType, 'Notification color must be specified')

        HipChatPublisherNotificationContext context = new HipChatPublisherNotificationContext()
        ContextHelper.executeInContext(notifyClosure, context)

        def notifyTypes = NotificationType.FAILURE..NotificationType.UNSTABLE
        def notifyEnable = context.notifyEnabled ?: notifyTypes.contains(notificationType) ? true : false
        this.notificationNodes << new NodeBuilder().'jenkins.plugins.hipchat.model.NotificationConfig' {
            notifyEnabled(notifyEnable)
            textFormat(context.textFormat)
            delegate.notificationType(notificationType.type)
            color(notificationColor.color)
            messageTemplate(context.messageTemplate ?: '')
        }
    }

    /**
     * Valid notification types.
     */
    static enum NotificationType {
        STARTED('STARTED'),
        SUCCESS('SUCCESS'),
        BACK_TO_NORMAL('BACK_TO_NORMAL'),
        FAILURE('FAILURE'),
        NOT_BUILT('NOT_BUILT'),
        ABORTED('ABORTED'),
        UNSTABLE('UNSTABLE')

        final String type

        NotificationType(String type) {
            this.type = type
        }
    }

    /**
     * Valid notification colors.
     */
    static enum NotificationColor {
        YELLOW('YELLOW'),
        GREEN('GREEN'),
        RED('RED'),
        PURPLE('PURPLE'),
        GRAY('GRAY'),
        RANDOM('RANDOM')

        final String color

        NotificationColor(String color) {
            this.color = color
        }
    }
}
