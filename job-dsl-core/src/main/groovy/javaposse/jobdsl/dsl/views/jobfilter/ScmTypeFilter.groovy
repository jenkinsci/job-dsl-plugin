package javaposse.jobdsl.dsl.views.jobfilter

class ScmTypeFilter extends AbstractJobFilter {
    ScmType type = ScmType.NULL

    /**
     * Select SCM type. Defaults to {@code ScmType.NULL}.
     */
    void type(ScmType type) {
        this.type = type
    }
}
