package javaposse.jobdsl.dsl.jobs

import javaposse.jobdsl.dsl.Item
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.WithXmlAction;
import javaposse.jobdsl.dsl.helpers.PromotionContext;

/**
 * @author Andrew Potter (ddcapotter)
 */
public class Promotion extends Item {

    PromotionContext context

    public Promotion(JobManagement jobManagement, PromotionContext context) {
        super(jobManagement);

        this.context = context
    }

    @Override
    public Node getNode() {
        Node root = getJobTemplate()

        if(context.configureClosure) {
            WithXmlAction configure = WithXmlAction.create(context.configureClosure)
            configure.execute(root)
        }

        return root
    }
}
