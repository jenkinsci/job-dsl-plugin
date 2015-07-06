package javaposse.jobdsl.dsl

class BuildBlockerContext implements Context {
    String blockLevel = 'UNDEFINED'
    String scanQueueFor = 'DISABLED'

    void blockLevel(String blockLevel) {
        this.blockLevel = blockLevel
    }

    void scanQueueFor(String scanQueueFor) {
        this.scanQueueFor = scanQueueFor
    }
}
