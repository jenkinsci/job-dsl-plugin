package javaposse.jobdsl.plugin.actions

import hudson.model.Action
import hudson.model.Item
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.plugin.LookupStrategy

import static javaposse.jobdsl.plugin.LookupStrategy.JENKINS_ROOT

class GeneratedJobsBuildAction extends GeneratedObjectsRunAction<GeneratedJob> {
    @SuppressWarnings('UnnecessaryTransientModifier')
    @Deprecated
    private transient Set<GeneratedJob> modifiedJobs

    protected final LookupStrategy lookupStrategy

    GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs, LookupStrategy lookupStrategy) {
        super(modifiedJobs)
        this.lookupStrategy = lookupStrategy
    }

    Set<Item> getItems() {
        Set<Item> result = new TreeSet<>(Comparators.ITEM_COMPARATOR)
        for (GeneratedJob job : modifiedObjects) {
            Item item = lookupStrategy.getItem(owner.parent, job.jobName, Item)
            if (item != null) {
                result << item
            }
        }
        result
    }

    @Override
    Collection<? extends Action> getProjectActions() {
        Collections.singleton(new GeneratedJobsAction(owner.parent))
    }

    @SuppressWarnings(['UnusedPrivateMethod', 'GroovyUnusedDeclaration'])
    private Object readResolve() {
        if (lookupStrategy == null || modifiedObjects == null) {
            new GeneratedJobsBuildAction(modifiedObjects ?: modifiedJobs, lookupStrategy ?: JENKINS_ROOT)
        } else {
            this
        }
    }
}
