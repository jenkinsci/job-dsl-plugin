package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.helpers.Context

class ViolationsContext implements Context {
    static validTypes = ['checkstyle', 'codenarc', 'cpd', 'cpplint', 'csslint', 'findbugs', 'fxcop', 'gendarme',
            'jcreport', 'jslint', 'pep8', 'perlcritic', 'pmd', 'pylint', 'simian', 'stylecop', 'jshint']

    private final Map<String, ViolationsEntry> entries = validTypes.collectEntries { [it, createEntry()] }
    String sourcePathPattern = null
    String fauxProjectPath = null
    Integer perFileDisplayLimit = null
    String sourceEncoding = null

    def sourcePathPattern(String sourcePathPattern) {
        this.sourcePathPattern = sourcePathPattern
    }

    def fauxProjectPath(String fauxProjectPath) {
        this.fauxProjectPath = fauxProjectPath
    }

    def perFileDisplayLimit(Integer perFileDisplayLimit) {
        this.perFileDisplayLimit = perFileDisplayLimit
    }

    def sourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding
    }

    private createEntry(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        new ViolationsEntry(
                min: min ?: 10,
                max: max ?: 999,
                unstable: unstable ?: 999,
                pattern: pattern)
    }

    private addEntry(String key, Integer min, Integer max, Integer unstable, String pattern) {
        entries[key] = createEntry(min, max, unstable, pattern)
    }

    def methodMissing(String key, args) {

        if (!validTypes.contains(key)) {
            throw new IllegalArgumentException("${key} is not a known type for the Violations plugin")
        }

        Integer min = null
        Integer max = null
        Integer unstable = null
        String pattern = null
        if (args.length > 0) {
            min = args[0]
        }
        if (args.length > 1) {
            max = args[1]
        }
        if (args.length > 2) {
            unstable = args[2]
        }
        if (args.length > 3) {
            pattern = args[3]
        }

        addEntry(key, min, max, unstable, pattern)
    }

    @Canonical
    static class ViolationsEntry {
        int min
        int max
        int unstable
        String pattern
    }
}
