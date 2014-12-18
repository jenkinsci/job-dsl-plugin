package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.Context

class ScpContext implements Context {
    final List<ScpEntry> entries = []

    void entry(String source, String destination = '', boolean keepHierarchy = false) {
        entries << new ScpEntry(source: source, destination: destination, keepHierarchy: keepHierarchy)
    }

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
