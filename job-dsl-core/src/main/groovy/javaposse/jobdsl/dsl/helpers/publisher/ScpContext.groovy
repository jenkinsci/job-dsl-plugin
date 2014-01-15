package javaposse.jobdsl.dsl.helpers.publisher

import javaposse.jobdsl.dsl.helpers.Context

class ScpContext implements Context {
    private List<ScpEntry> entries = []

    def entry(String source, String destination = '', boolean keepHierarchy = false) {
        entries << new ScpEntry(source: source, destination: destination, keepHierarchy: keepHierarchy)
    }

    static class ScpEntry {
        String source
        String destination
        boolean keepHierarchy
    }
}


