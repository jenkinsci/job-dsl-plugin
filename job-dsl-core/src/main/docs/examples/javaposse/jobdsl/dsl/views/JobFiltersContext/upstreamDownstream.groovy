listView('example') {
    jobFilters {
        upstreamDownstream {
            includeDownstream()
            includeUpstream()
            recursive()
            excludeOriginals()
        }
    }
}
