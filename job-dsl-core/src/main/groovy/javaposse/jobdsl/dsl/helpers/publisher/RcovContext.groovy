package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.Context

class RcovContext implements Context {
    private static final VALID_TYPES = ['TOTAL_COVERAGE', 'CODE_COVERAGE']

    final Map<String, MetricEntry> entries = VALID_TYPES.collectEntries { [it, createEntry()] }
    String reportDirectory

    /**
     * Resolves class names to source file names.
     */
    void reportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory
    }

    /**
     * Configure health reporting thresholds for the total coverage.
     */
    void totalCoverage(int healthy, int unhealthy, int unstable) {
        addEntry('TOTAL_COVERAGE', healthy, unhealthy, unstable)
    }

    /**
     * Configure health reporting thresholds for the code coverage.
     */
    void codeCoverage(int healthy, int unhealthy, int unstable) {
        addEntry('CODE_COVERAGE', healthy, unhealthy, unstable)
    }

    private static createEntry(int healthy = 80, int unhealthy = 0, int unstable = 0) {
        new MetricEntry(
                healthy: healthy ?: 80,
                unhealthy: unhealthy ?: 0,
                unstable: unstable ?: 0,
        )
    }

    private addEntry(String key, int healthy, int unhealthy, int unstable) {
        entries[key] = createEntry(healthy, unhealthy, unstable)
    }

    @Canonical
    static class MetricEntry {
        int healthy
        int unhealthy
        int unstable
    }
}
