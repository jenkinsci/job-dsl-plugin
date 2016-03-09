package javaposse.jobdsl.dsl.views.jobfilter

class BuildTrendFilter extends AbstractJobFilter {
    BuildCountType buildCountType = BuildCountType.LATEST
    AmountType amountType = AmountType.HOURS
    BigDecimal amount = BigDecimal.ZERO
    BuildStatusType status = BuildStatusType.COMPLETED

    /**
     * Selects the build count type to be matched. Defaults to {@code BuildCountType.LATEST}.
     */
    void buildCountType(BuildCountType buildCountType) {
        this.buildCountType = buildCountType
    }

    /**
     * Selects the amount type to be matched. Defaults to {@code BuildCountType.HOURS}.
     */
    void amountType(AmountType amountType) {
        this.amountType = amountType
    }

    /**
     * Selects the amount to be matched. Defaults to {@code 0}.
     */
    void amount(BigDecimal amount) {
        this.amount = amount
    }

    /**
     * Selects the build status to be matched. Defaults to {@code BuildStatusType.COMPLETED}.
     */
    void status(BuildStatusType status) {
        this.status = status
    }
}
