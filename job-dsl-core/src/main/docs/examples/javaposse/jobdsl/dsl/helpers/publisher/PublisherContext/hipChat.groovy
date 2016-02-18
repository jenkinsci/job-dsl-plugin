job('example') {
    publishers {
        hipChat {
            rooms('Dev Team A', 'QA')
            notification(NotificationType.STARTED, NotificationColor.YELLOW) {}
            notification(NotificationType.SUCCESS, NotificationColor.GREEN) {}
            notification(NotificationType.FAILURE, NotificationColor.RED) {}
            notification(NotificationType.UNSTABLE, NotificationColor.RED) {}
            notification(NotificationType.ABORTED, NotificationColor.GRAY) {}
        }
    }
}
