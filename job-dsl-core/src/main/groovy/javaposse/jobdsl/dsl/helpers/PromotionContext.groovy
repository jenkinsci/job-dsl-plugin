package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.jobs.Promotion

/**
 * @author Andrew Potter (ddcapotter)
 */
class PromotionContext implements Context {

    private final JobManagement jobManagement
    Closure configureClosure
    String name

    PromotionContext(JobManagement jobManagement, String name) {
        this.name = name
        this.jobManagement = jobManagement
    }

    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }

    Promotion createPromotion() {
        new Promotion(jobManagement, this)
    }
}
