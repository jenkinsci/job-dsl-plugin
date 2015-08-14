package javaposse.jobdsl.dsl.helpers.parameter

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceReactiveReferenceContext extends ActiveChoiceReferenceContext {

    private static final Set<String> VALID_CHOICE_TYPES = [
            'FORMATTED_HTML', 'FORMATTED_HIDDEN_HTML', 'TEXT_BOX', 'ORDERED_LIST', 'UNORDERED_LIST'
    ]

    boolean supportsFilterable = false
    String choiceTypePrefix = 'ET_'

    boolean omitValueField
    String choiceType = 'FORMATTED_HTML'

    void omitValueField(boolean omitValueField = true) {
        this.omitValueField = omitValueField
    }

    @Override
    void filterable(boolean filterable) {
        checkArgument(false, 'activeChoiceReactiveReferenceParam does not support filtering')
    }

    @Override
    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
        this.choiceType = choiceType
    }
}
