package javaposse.jobdsl.plugin;

import com.google.common.collect.Sets;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.RunAction;
import hudson.util.XStream2;
import javaposse.jobdsl.dsl.GeneratedJob;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class GeneratedJobsBuildAction implements RunAction {
    private transient AbstractBuild<?,?> owner;
    public final Set<GeneratedJob> modifiedJobs;
    private final JobLookupStrategy jobLookupStrategy;

    public GeneratedJobsBuildAction(Collection<GeneratedJob> modifiedJobs, JobLookupStrategy jobLookupStrategy) {
        this.modifiedJobs = Sets.newLinkedHashSet(modifiedJobs);
        this.jobLookupStrategy = jobLookupStrategy;
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

    private JobLookupStrategy getJobLookupStrategy() {
        // Provide backwards compatible behaviour for existing seed jobs.
        return jobLookupStrategy != null ? jobLookupStrategy : JobLookupStrategy.JENKINS_ROOT;
    }

    public Collection<GeneratedJob> getModifiedJobs() {
        return modifiedJobs;
    }

    public Collection<AbstractProject> getModifiedProjects() {
        Set<AbstractProject> modifiedProjects = Sets.newLinkedHashSet();
        if (owner != null && modifiedJobs != null) {
            for (GeneratedJob modifiedJob : modifiedJobs) {
                Item modifiedProject = getJobLookupStrategy().getItem(owner.getProject(), modifiedJob.getJobName());
                if (modifiedProject instanceof AbstractProject) {
                    modifiedProjects.add((AbstractProject) modifiedProject);
                }
            }
        }
        return modifiedProjects;
    }

    @Override
    public void onAttached(Run run) {
        if (run instanceof AbstractBuild) {
            owner = (AbstractBuild) run;
        }
    }

    @Override
    public void onLoad() {
        // noop
    }

    @Override
    public void onBuildComplete() {
        // noop
    }

    // TODO Once we depend on Jenkins version 1.509.3 or higher we can implement the RunAction2 interface to set the AbstractBuild on load, instead of using this Converter.
    public static class ConverterImpl extends XStream2.PassthruConverter<GeneratedJobsBuildAction> {
        public ConverterImpl(XStream2 xstream) {
            super(xstream);
        }

        @Override protected void callback(GeneratedJobsBuildAction action, UnmarshallingContext context) {
            Iterator keys = context.keys();
            while (keys.hasNext()) {
                Object run = context.get(keys.next());
                if (run instanceof AbstractBuild) {
                    action.owner = (AbstractBuild) run;
                    return;
                }
            }
        }
    }
}
