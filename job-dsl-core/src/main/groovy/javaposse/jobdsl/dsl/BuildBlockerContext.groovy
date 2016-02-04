package javaposse.jobdsl.dsl

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class BuildBlockerContext extends AbstractContext {
    private static final Set<String> VALID_BLOCK_LEVELS = ['GLOBAL', 'NODE']
    private static final Set<String> VALID_QUEUE_SCAN_SCOPES = ['ALL', 'BUILDABLE', 'DISABLED']

    String blockLevel = 'NODE'
    String scanQueueFor = 'DISABLED'

    protected BuildBlockerContext(JobManagement jobManagement) {
        super(jobManagement)
    }

    /**
     * Possible values are {@code 'GLOBAL'} and {@code 'NODE'} (default).
     */
    void blockLevel(String blockLevel) {
        checkArgument(
                VALID_BLOCK_LEVELS.contains(blockLevel),
                "blockLevel must be one of ${VALID_BLOCK_LEVELS.join(', ')}"
        )
        this.blockLevel = blockLevel
    }

    /**
     * Possible values are {@code 'ALL'}, {@code 'BUILDABLE'} and {@code 'DISABLED'} (default).
     */
    void scanQueueFor(String scanQueueFor) {
        checkArgument(
                VALID_QUEUE_SCAN_SCOPES.contains(scanQueueFor),
                "scanQueueFor must be one of ${VALID_QUEUE_SCAN_SCOPES.join(', ')}"
        )
        this.scanQueueFor = scanQueueFor
    }
}
