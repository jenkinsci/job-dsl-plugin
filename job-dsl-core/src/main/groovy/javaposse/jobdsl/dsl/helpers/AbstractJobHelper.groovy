package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.JobType
import javaposse.jobdsl.dsl.WithXmlAction

/**
 * Base for all job helpers
 */
public class AbstractJobHelper extends AbstractHelper {

    JobType type

    AbstractJobHelper(List<WithXmlAction> withXmlActions, JobType type) {
        super(withXmlActions)
        this.type = type
    }

}
