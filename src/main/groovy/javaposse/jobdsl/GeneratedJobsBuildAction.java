package javaposse.jobdsl;

import hudson.model.AbstractProject;
import hudson.model.Action;


class GeneratedJobsBuildAction implements Action {
    public final AbstractProject<?,?> project;

    public GeneratedJobsBuildAction(AbstractProject<?,?> project) {
        this.project = project;
    }

    /**
     * No task list item.
     */
    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return "Generated Jobs";
    }

    public String getUrlName() {
        return "generatedJobs";
    }

//    public GeneratedJobsAction getLastTestResultAction() {
//        final AbstractBuild<?,?> tb = project.getLastSuccessfulBuild();
//
//        AbstractBuild<?,?> b=project.getLastBuild();
//        while(b!=null) {
//            GeneratedJobsAction a = b.getGeneratedJobsAction();
//            if (a!=null && !b.isBuilding()) {
//                return a;
//            }
//
//            if(b==tb) {
//                // if even the last successful build didn't produce the test result,
//                // that means we just don't have any tests configured.
//                return null;
//            }
//            b = b.getPreviousBuild();
//        }
//
//        return null;
//    }
}
