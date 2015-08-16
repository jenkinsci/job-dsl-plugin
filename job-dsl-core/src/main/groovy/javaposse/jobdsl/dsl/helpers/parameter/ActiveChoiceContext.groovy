package javaposse.jobdsl.dsl.helpers.parameter

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceContext extends AbstractActiveChoiceContext {
    private static final Set<String> VALID_CHOICE_TYPES = [
        'SINGLE_SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO'
    ]

    boolean filterable
    String choiceType = 'SINGLE_SELECT'

    void filterable(boolean filterable = true) {
        this.filterable = filterable
    }

    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
      this.choiceType = choiceType
    }
}
