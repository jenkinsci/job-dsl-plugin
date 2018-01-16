package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class RcovContext implements Context {
    private static final Set<String> VALID_TYPES = ['TOTAL_COVERAGE', 'CODE_COVERAGE']

    final Map<String, MetricEntry> entries = VALID_TYPES.collectEntries { [it, createEntry()] }
    String reportDirectory

    /**
     * Sets the relative path to the coverage report directory.
     */
    void reportDirectory(String reportDirectory) {
        this.reportDirectory = reportDirectory
    }

    /**
     * Defines health reporting thresholds for the total coverage. {@code healthy} defaults to {@code 80},
     * {@code unhealthy} and {@code unstable} default to {@code 0}.
     */
    void totalCoverage(int healthy, int unhealthy, int unstable) {
        addEntry('TOTAL_COVERAGE', healthy, unhealthy, unstable)
    }

    /**
     * Defines health reporting thresholds for the code coverage. {@code healthy} defaults to {@code 80},
     * {@code unhealthy} and {@code unstable} default to {@code 0}.
     */
    void codeCoverage(int healthy, int unhealthy, int unstable) {
        addEntry('CODE_COVERAGE', healthy, unhealthy, unstable)
    }

    private static MetricEntry createEntry(int healthy = 80, int unhealthy = 0, int unstable = 0) {
        new MetricEntry(healthy: healthy, unhealthy: unhealthy, unstable: unstable)
    }

    private void addEntry(String key, int healthy, int unhealthy, int unstable) {
        entries[key] = createEntry(healthy, unhealthy, unstable)
    }

    static class MetricEntry {
        int healthy
        int unhealthy
        int unstable
    }
}
