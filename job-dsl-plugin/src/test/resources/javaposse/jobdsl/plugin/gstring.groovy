def TRIGGER = '@midnight'

job('a') {
    triggers {
        timerTrigger {
            spec("${TRIGGER}")
        }
    }
}
