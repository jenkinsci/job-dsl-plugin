package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction
import javaposse.jobdsl.dsl.helpers.PromotionContext

/**
 * @author Andrew Potter (ddcapotter)
 */
class Promotion extends Item {

    PromotionContext context

    Promotion(JobManagement jobManagement, PromotionContext context) {
        super(jobManagement)

        this.context = context
    }

    @Override
    Node getNode() {
        Node root = loadJobTemplate()

        if (context.configureClosure) {
            WithXmlAction configure = WithXmlAction.create(context.configureClosure)
            configure.execute(root)
        }

        root
    }
}
