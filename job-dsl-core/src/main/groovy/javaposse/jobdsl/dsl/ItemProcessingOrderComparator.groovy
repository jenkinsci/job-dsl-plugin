package javaposse.jobdsl.dsl

import javaposse.jobdsl.dsl.jobs.MultiJob

/**
 * Sorts items so that folders come first and MultiJobs come last.
 */
class ItemProcessingOrderComparator implements Comparator<Item> {
    @Override
    int compare(Item item1, Item item2) {
        if (item1 instanceof Folder && !(item2 instanceof Folder)) {
            return -1
        } else if (!(item1 instanceof Folder) && item2 instanceof Folder) {
            return 1
        } else if (item1 instanceof Job && item2 instanceof Job) {
            return compareJobs((Job) item1, (Job) item2)
        } else {
            return 0
        }
    }

    private static int compareJobs(Job job1, Job job2) {
        if (job1 instanceof MultiJob && !(job2 instanceof MultiJob)) {
            return 1
        } else if (!(job1 instanceof MultiJob) && job2 instanceof MultiJob) {
            return -1
        } else {
            return 0
        }
    }
}
