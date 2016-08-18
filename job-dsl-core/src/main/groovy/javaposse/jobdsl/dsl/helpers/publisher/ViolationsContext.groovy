package javaposse.jobdsl.dsl.helpers.publisher

import groovy.transform.Canonical
import javaposse.jobdsl.dsl.Context

class ViolationsContext implements Context {
    private static final VALID_TYPES = [
            'checkstyle', 'codenarc', 'cpd', 'cpplint', 'csslint', 'findbugs', 'fxcop', 'gendarme',
            'jcreport', 'jslint', 'pep8', 'perlcritic', 'pmd', 'pylint', 'simian', 'stylecop', 'jshint'
    ]

    final Map<String, ViolationsEntry> entries = VALID_TYPES.collectEntries { [it, createEntry()] }
    String sourcePathPattern = null
    String fauxProjectPath = null
    Integer perFileDisplayLimit = null
    String sourceEncoding = null

    /**
     * Resolves class names to source file names.
     */
    void sourcePathPattern(String sourcePathPattern) {
        this.sourcePathPattern = sourcePathPattern
    }

    /**
     * Sets an alternative project directory for the reporting engine.
     */
    void fauxProjectPath(String fauxProjectPath) {
        this.fauxProjectPath = fauxProjectPath
    }

    /**
     * Limits the number of violations displayed per file and violation type.
     */
    void perFileDisplayLimit(Integer perFileDisplayLimit) {
        this.perFileDisplayLimit = perFileDisplayLimit
    }

    /**
     * Sets the encoding to use for reading source files.
     */
    void sourceEncoding(String sourceEncoding) {
        this.sourceEncoding = sourceEncoding
    }

    /**
     * Configures the violations report for Checkstyle.
     */
    void checkstyle(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('checkstyle', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for CodeNarc.
     */
    void codenarc(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('codenarc', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for CPD.
     */

    void cpd(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('cpd', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for cpplint.
     */

    void cpplint(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('cpplint', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for CSS Lint.
     */
    void csslint(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('csslint', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for FindBugs.
     */
    void findbugs(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('findbugs', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for FxCop.
     */
    void fxcop(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('fxcop', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for Gendarme.
     */
    void gendarme(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('gendarme', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for JcReport.
     */
    void jcreport(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('jcreport', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for JSLint.
     */
    void jslint(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('jslint', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for PEP8.
     */
    void pep8(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('pep8', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for Perl::Critic.
     */
    void perlcritic(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('perlcritic', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for PMD.
     */
    void pmd(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('pmd', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for Pylint.
     */
    void pylint(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('pylint', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for Simian.
     */
    void simian(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('simian', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for StyleCop.
     */
    void stylecop(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('stylecop', min, max, unstable, pattern)
    }

    /**
     * Configures the violations report for JSHint.
     */
    void jshint(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        addEntry('jshint', min, max, unstable, pattern)
    }

    private static createEntry(Integer min = null, Integer max = null, Integer unstable = null, String pattern = null) {
        new ViolationsEntry(
                min: min == null ? 10 : min,
                max: max == null ? 999 : max,
                unstable: unstable == null ? 999 : unstable,
                pattern: pattern
        )
    }

    private addEntry(String key, Integer min, Integer max, Integer unstable, String pattern) {
        entries[key] = createEntry(min, max, unstable, pattern)
    }

    @Canonical
    static class ViolationsEntry {
        int min
        int max
        int unstable
        String pattern
    }
}
