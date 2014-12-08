package javaposse.jobdsl.plugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Descriptor;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import hudson.util.ListBoxModel;
import jenkins.YesNoMaybe;

@Extension(dynamicLoadable = YesNoMaybe.YES)
public class DescriptorImpl extends BuildStepDescriptor<Builder> {

    private Multimap<String, SeedReference> templateJobMap; // K=templateName, V=Seed

    public DescriptorImpl() {
        super(ExecuteDslScripts.class);
        load();
    }

    public String getDisplayName() {
        return "Process Job DSLs";
    }

    public Multimap<String, SeedReference> getTemplateJobMap() {
        if (templateJobMap == null) {
            templateJobMap = HashMultimap.create();
        }

        return templateJobMap;
    }

    public void setTemplateJobMap(Multimap<String, SeedReference> templateJobMap) {
        this.templateJobMap = templateJobMap;
    }

    public ListBoxModel doFillRemovedJobActionItems() {
        ListBoxModel items = new ListBoxModel();
        for (RemovedJobAction action : RemovedJobAction.values()) {
            items.add(action.getDisplayName(), action.name());
        }
        return items;
    }

    public ListBoxModel doFillLookupStrategyItems() {
        ListBoxModel items = new ListBoxModel();
        for (LookupStrategy item : LookupStrategy.values()) {
            items.add(item.getDisplayName(), item.name());
        }
        return items;
    }
    
    @Override
    public boolean isApplicable(Class<? extends AbstractProject> jobType) {
		return true;
	}
}
