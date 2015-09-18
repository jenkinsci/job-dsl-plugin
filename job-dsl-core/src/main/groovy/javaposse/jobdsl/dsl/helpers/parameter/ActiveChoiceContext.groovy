package javaposse.jobdsl.dsl.helpers.parameter

import static javaposse.jobdsl.dsl.Preconditions.checkArgument

class ActiveChoiceContext extends AbstractActiveChoiceContext {
    private static final Set<String> VALID_CHOICE_TYPES = [
        'SINGLE_SELECT', 'MULTI_SELECT', 'CHECKBOX', 'RADIO'
    ]

    boolean filterable
    String choiceType = 'SINGLE_SELECT'

    /**
     * If set, provides a text box filter in the UI control where a text filter can be typed.
     */
    void filterable(boolean filterable = true) {
        this.filterable = filterable
    }

    /**
     * Selects one of four different rendering options for the option values.
     *
     * Must be one of {@code 'SINGLE_SELECT'} (default), {@code 'MULTI_SELECT'}, {@code 'CHECKBOX'} or {@code 'RADIO'}.
     */
    void choiceType(String choiceType) {
        checkArgument(
                VALID_CHOICE_TYPES.contains(choiceType),
                "choiceType must be one of ${VALID_CHOICE_TYPES.join(', ')}"
        )
        this.choiceType = choiceType
    }
}
