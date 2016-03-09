package javaposse.jobdsl.dsl.views.jobfilter

import javaposse.jobdsl.dsl.Context

class FallbackFilter implements Context {
    FallbackType type = FallbackType.ADD_ALL_IF_NONE_INCLUDED

    /**
     * Selects the fallback type to be matched. Defaults to {@code FallbackType.ADD_ALL_IF_NONE_INCLUDED}.
     * <ul>
     *     <li>Add all jobs if no jobs are included</li>
     *     <li>Remove all jobs if all jobs are included</li>
     * </ul>
     */
    void type(FallbackType fallbackType) {
        this.type = fallbackType
    }
}
