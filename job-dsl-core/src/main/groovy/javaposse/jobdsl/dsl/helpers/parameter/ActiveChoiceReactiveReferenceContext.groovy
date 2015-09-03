package javaposse.jobdsl.dsl.helpers.parameter

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceReactiveReferenceContext extends AbstractActiveChoiceContext {
    private static final Set<String> VALID_CHOICE_TYPES = [
            'FORMATTED_HTML', 'FORMATTED_HIDDEN_HTML', 'TEXT_BOX', 'ORDERED_LIST', 'UNORDERED_LIST'
    ]

    Set<String> referencedParameters = []
    boolean omitValueField
    String choiceType = 'TEXT_BOX'

    /**
     * Specifies a list of job parameters that trigger an auto-refresh.
     */
    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }

    /**
     * Omits the hidden value field.
     */
    void omitValueField(boolean omitValueField = true) {
        this.omitValueField = omitValueField
    }

    /**
     * Selects one of four different rendering options for the option values.
     *
     * Must be one of {@code 'TEXT_BOX'} (default), {@code 'FORMATTED_HTML'}, {@code 'FORMATTED_HIDDEN_HTML'},
     * {@code 'ORDERED_LIST'} or {@code 'UNORDERED_LIST'}.
     */
    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
        this.choiceType = choiceType
    }
}
