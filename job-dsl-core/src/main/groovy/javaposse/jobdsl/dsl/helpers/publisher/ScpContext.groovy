package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ScpContext implements Context {
    final List<ScpEntry> entries = []

    /**
     * Specifies the files to upload. Can be called multiple times to upload more files.
     */
    void entry(String source, String destination = '', boolean keepHierarchy = false) {
        entries << new ScpEntry(source: source, destination: destination, keepHierarchy: keepHierarchy)
    }

    /**
     * Specifies the files to upload. Can be called multiple times to upload more files.
     *
     * @since 1.27
     */
    void entries(Iterable<String> sources, String destination = '', boolean keepHierarchy = false) {
        sources.each { source ->
            entry(source, destination, keepHierarchy)
        }
    }

    static class ScpEntry {
        String source
        String destination
        boolean keepHierarchy
    }
}
