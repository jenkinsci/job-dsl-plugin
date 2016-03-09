package javaposse.jobdsl.dsl.views.jobfilter

class BuildDurationFilter extends AbstractJobFilter {
    BuildCountType buildCountType = BuildCountType.LATEST
    AmountType amountType = AmountType.HOURS
    BigDecimal amount = BigDecimal.ZERO
    boolean lessThan
    BigDecimal buildDuration = BigDecimal.ZERO

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
     * true : Less than.
     * false : more than.
     * Defaults to {@code false}.
     */
    void lessThan(boolean lessThan = true) {
        this.lessThan = lessThan
    }

    /**
     * Selects the build duration in minutes to be matched. Defaults to {@code 0}.
     */
    void buildDuration(BigDecimal buildDuration) {
        this.buildDuration = buildDuration
    }
}
