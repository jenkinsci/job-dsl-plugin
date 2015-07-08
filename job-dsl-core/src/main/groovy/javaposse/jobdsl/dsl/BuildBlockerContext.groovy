package javaposse.jobdsl.dsl

import static com.google.common.base.Preconditions.checkArgument

class BuildBlockerContext implements Context {
    private static final Set<String> VALID_BLOCK_LEVELS = ['GLOBAL', 'NODE']
    private static final Set<String> VALID_QUEUE_SCAN_SCOPES = ['ALL', 'BUILDABLE', 'DISABLED']

    String blockLevel = 'NODE'
    String scanQueueFor = 'DISABLED'

    void blockLevel(String blockLevel) {
        checkArgument(
                VALID_BLOCK_LEVELS.contains(blockLevel),
                "blockLevel must be one of ${VALID_BLOCK_LEVELS.join(', ')}"
        )
        this.blockLevel = blockLevel
    }

    void scanQueueFor(String scanQueueFor) {
        checkArgument(
                VALID_QUEUE_SCAN_SCOPES.contains(scanQueueFor),
                "scanQueueFor must be one of ${VALID_QUEUE_SCAN_SCOPES.join(', ')}"
        )
        this.scanQueueFor = scanQueueFor
    }
}
