package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

class UpstreamDownstreamJobsFilter implements Context {
    boolean includeDownstream
    boolean includeUpstream
    boolean recursive
    boolean excludeOriginals

    /**
     * Include downstream jobs. Defaults to {@code false}.
     */
    void includeDownstream(boolean includeDownstream = true) {
        this.includeDownstream = includeDownstream
    }

    /**
     * Include upstream jobs. Defaults to {@code false}.
     */
    void includeUpstream(boolean includeUpstream = true) {
        this.includeUpstream = includeUpstream
    }

    /**
     * Include upstream/downstream jobs recursively. Defaults to {@code false}.
     */
    void recursive(boolean recursive = true) {
        this.recursive = recursive
    }

    /**
     * Do not show source jobs. Defaults to {@code false}.
     */
    void excludeOriginals(boolean excludeOriginals = true) {
        this.excludeOriginals = excludeOriginals
    }
}
