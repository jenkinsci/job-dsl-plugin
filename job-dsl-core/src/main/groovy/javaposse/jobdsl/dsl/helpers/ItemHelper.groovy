package javaposse.jobdsl.dsl.helpers

import java.util.List

import com.google.common.base.Preconditions;

import javaposse.jobdsl.dsl.JobType;
import javaposse.jobdsl.dsl.WithXmlAction;

class ItemHelper extends AbstractHelper {

    public ItemHelper(List<WithXmlAction> withXmlActions) {
        super(withXmlActions)
    }

    def description(String descriptionString) {
        execute {
            def descNode = methodMissing('description', descriptionString)
            it / descNode
        }
    }

    /**
     * Sets a display name for the project.
     *
     * @param displayName name to display
     */
    def displayName(String displayName) {
        def name = Preconditions.checkNotNull(displayName, 'Display name must not be null.')
        execute {
            def node = methodMissing('displayName', name)
            it / node
        }

    }

}
