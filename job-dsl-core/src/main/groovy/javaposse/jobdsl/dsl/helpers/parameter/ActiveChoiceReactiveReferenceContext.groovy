package javaposse.jobdsl.dsl.helpers.parameter

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceReactiveReferenceContext extends AbstractActiveChoiceContext {
    private static final Set<String> VALID_CHOICE_TYPES = [
            'FORMATTED_HTML', 'FORMATTED_HIDDEN_HTML', 'TEXT_BOX', 'ORDERED_LIST', 'UNORDERED_LIST'
    ]

    Set<String> referencedParameters = []
    boolean omitValueField
    String choiceType = 'TEXT_BOX'

    void referencedParameter(String referencedParameters) {
        this.referencedParameters << referencedParameters
    }

    void omitValueField(boolean omitValueField = true) {
        this.omitValueField = omitValueField
    }

    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
        this.choiceType = choiceType
    }
}
