package javaposse.jobdsl.dsl.views.jobfilter

class UserRelevanceFilter extends AbstractJobFilter {
    BuildCountType buildCountType = BuildCountType.LATEST
    AmountType amountType = AmountType.HOURS
    BigDecimal amount = BigDecimal.ZERO
    boolean matchUserId
    boolean matchUserFullName
    boolean ignoreCase
    boolean ignoreWhitespace
    boolean ignoreNonAlphaNumeric
    boolean matchBuilder
    boolean matchEmail
    boolean matchScmChanges

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
     * Defaults to {@code false}.
     */
    void matchUserId(boolean matchUserId = true) {
        this.matchUserId = matchUserId
    }

    /**
     * Defaults to {@code false}.
     */
    void matchUserFullName(boolean matchUserFullName = true) {
        this.matchUserFullName = matchUserFullName
    }

    /**
     * Defaults to {@code false}.
     */
    void ignoreCase(boolean ignoreCase = true) {
        this.ignoreCase = ignoreCase
    }

    /**
     * Defaults to {@code false}.
     */
    void ignoreWhitespace(boolean ignoreWhitespace = true) {
        this.ignoreWhitespace = ignoreWhitespace
    }

    /**
     * Defaults to {@code false}.
     */
    void ignoreNonAlphaNumeric(boolean ignoreNonAlphaNumeric = true) {
        this.ignoreNonAlphaNumeric = ignoreNonAlphaNumeric
    }

    /**
     * Defaults to {@code false}.
     */
    void matchBuilder(boolean matchBuilder = true) {
        this.matchBuilder = matchBuilder
    }

    /**
     * Defaults to {@code false}.
     */
    void matchEmail(boolean matchEmail = true) {
        this.matchEmail = matchEmail
    }

    /**
     * Defaults to {@code false}.
     */
    void matchScmChanges(boolean matchScmChanges = true) {
        this.matchScmChanges = matchScmChanges
    }
}
