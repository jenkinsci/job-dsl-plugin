package javaposse.jobdsl.plugin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import hudson.Extension;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.AbstractProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.listeners.ItemListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.ListBoxModel;
import javaposse.jobdsl.plugin.actions.GeneratedConfigFilesBuildAction;
import javaposse.jobdsl.plugin.actions.GeneratedJobsBuildAction;
import javaposse.jobdsl.plugin.actions.GeneratedViewsBuildAction;
import jenkins.YesNoMaybe;
import jenkins.model.Jenkins;
import org.jenkinsci.Symbol;

import java.util.Map;

@Extension(dynamicLoadable = YesNoMaybe.YES)
@Symbol("jobDsl")
public class DescriptorImpl extends BuildStepDescriptor<Builder> {
    private Multimap<String, SeedReference> templateJobMap; // K=templateName, V=Seed
    private Map<String, SeedReference> generatedJobMap;

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

    public Map<String, SeedReference> getGeneratedJobMap() {
        if (generatedJobMap == null) {
            generatedJobMap = Maps.newConcurrentMap();
        }

        return generatedJobMap;
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

    public ListBoxModel doFillRemovedViewActionItems() {
        ListBoxModel items = new ListBoxModel();
        for (RemovedViewAction action : RemovedViewAction.values()) {
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

    @Initializer(before = InitMilestone.PLUGINS_STARTED)
    public static void addAliases() {
        Run.XSTREAM2.addCompatibilityAlias(
                "javaposse.jobdsl.plugin.GeneratedConfigFilesBuildAction", GeneratedConfigFilesBuildAction.class
        );
        Run.XSTREAM2.addCompatibilityAlias(
                "javaposse.jobdsl.plugin.GeneratedJobsBuildAction", GeneratedJobsBuildAction.class
        );
        Run.XSTREAM2.addCompatibilityAlias(
                "javaposse.jobdsl.plugin.GeneratedViewsBuildAction", GeneratedViewsBuildAction.class
        );
    }

    private static void removeSeedReference(String key) {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        SeedReference seedReference = descriptor.getGeneratedJobMap().remove(key);
        if (seedReference != null) {
            descriptor.save();
        }
    }

    @Extension
    public static class GeneratedJobMapItemListener extends ItemListener {
        @Override
        public void onDeleted(Item item) {
            removeSeedReference(item.getFullName());
        }

        @Override
        public void onLocationChanged(Item item, String oldFullName, String newFullName) {
            removeSeedReference(oldFullName);
        }
    }
}
