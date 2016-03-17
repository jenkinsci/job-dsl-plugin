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

job('example2') {
    publishers {
        hipChat {
            rooms('Dev Team A', 'QA')
            notification(NotificationType.STARTED, NotificationColor.YELLOW) {
                messageTemplaAte('<a href="$URL"><b>$JOB_NAME</b></a> $STATUS <b>$GIT_BRANCH</b> ' +
                        '<a href="$URL/console">[OPEN]</a><br><i>$CHANGES_OR_CAUSE</i>')
            }
            notification(NotificationType.SUCCESS, NotificationColor.GREEN) {
                messageTemplate('<a href="$URL"><b>$JOB_NAME</b></a> $STATUS after $DURATION ' +
                        '<b>$GIT_BRANCH</b> <a href="$URL/console">[OPEN]</a>')
                notifyEnabled(true)
            }
            notification(NotificationType.FAILURE, NotificationColor.RED) {
                messageTemplate('<a href="$URL"><b>$JOB_NAME</b></a> $STATUS after $DURATION ' +
                        '<b>$GIT_BRANCH</b> <a href="$URL/console">[OPEN]</a>')
                notifyEnabled(true)
            }
            notification(NotificationType.UNSTABLE, NotificationColor.RED) {
                messageTemplate('<a href="$URL"><b>$JOB_NAME</b></a> $STATUS after $DURATION ' +
                        '<b>$GIT_BRANCH</b> <a href="$URL/console">[OPEN]</a>')
            }
            notification(NotificationType.ABORTED, NotificationColor.GRAY) {
                messageTemplate('<a href="$URL"><b>$JOB_NAME</b></a> $STATUS after $DURATION ' +
                        '<b>$GIT_BRANCH</b> <a href="$URL/console">[OPEN]</a>')
            }
        }
    }
}
