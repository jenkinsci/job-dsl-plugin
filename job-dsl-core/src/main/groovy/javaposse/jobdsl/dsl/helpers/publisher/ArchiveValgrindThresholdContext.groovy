package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ArchiveValgrindThresholdContext implements Context {
    String invalidReadWrite = ''
    String definitelyLost = ''
    String total = ''

    /**
     * Sets the number of invalid read/write operations threshold.
     * Defaults to ''
     */
    void invalidReadWrite(Integer invalidReadWrite) {
        this.invalidReadWrite = invalidReadWrite.toString()
    }

    /**
     * Sets the number of definitely lost memory buffers threshold.
     * Defaults to ''
     */
    void definitelyLost(Integer definitelyLost) {
        this.definitelyLost = definitelyLost.toString()
    }

    /**
     * Sets the total number of warnings threshold.
     * Defaults to ''
     */
    void total(Integer total) {
        this.total = total.toString()
    }
}
