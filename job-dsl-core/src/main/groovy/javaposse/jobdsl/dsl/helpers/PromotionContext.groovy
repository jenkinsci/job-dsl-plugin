package javaposse.jobdsl.dsl.helpers

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.jobs.Promotion;

/**
 * @author Andrew Potter (ddcapotter)
 */
public class PromotionContext implements Context {

    Closure configureClosure
    String name
    Promotion promotion

    public PromotionContext(JobManagement jobManagement, String name) {
        this.name = name
        this.promotion = new Promotion(jobManagement, this)
    }

    void configure(Closure configureClosure) {
        this.configureClosure = configureClosure
    }
}
