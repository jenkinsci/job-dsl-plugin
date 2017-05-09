package javaposse.jobdsl.plugin;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import hudson.Extension;
import hudson.Util;
import hudson.XmlFile;
import hudson.model.AbstractItem;
import hudson.model.AbstractProject;
import hudson.model.BuildableItem;
import hudson.model.Cause;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import jenkins.YesNoMaybe;
import jenkins.model.Jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

@Extension(dynamicLoadable = YesNoMaybe.YES)
public class MonitorTemplateJobs extends SaveableListener {
    private static final Logger LOGGER = Logger.getLogger(MonitorTemplateJobs.class.getName());

    @Override
    public void onChange(Saveable saveable, XmlFile file) {
        LOGGER.finest("onChange");

        if (!AbstractProject.class.isAssignableFrom(saveable.getClass())) {
            LOGGER.finest(String.format("%s is not a Project", saveable.getClass()));
            return;
        }

        // Look for template jobs
        AbstractItem project = (AbstractItem) saveable;
        String possibleTemplateName = project.getName();

        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        if (descriptor == null) {
            LOGGER.warning("Unable to get DescriptorImpl");
        }

        Multimap<String,SeedReference> templateJobMap = (descriptor != null? descriptor.getTemplateJobMap(): null);
        if (templateJobMap == null) {
            LOGGER.warning("Descriptor returned no template job map.");
        }

        Collection<SeedReference> seedJobReferences = templateJobMap != null? templateJobMap.get(possibleTemplateName): Collections.<SeedReference>emptyList();
        if (seedJobReferences.isEmpty()) {
            return;
        }

        final String digest;
        try {
            digest = Util.getDigestOf(new FileInputStream(project.getConfigFile().getFile()));
        } catch (IOException e) {
            LOGGER.warning(String.format("Unable to read template %s, which means we wouldn't be able to run seed anyways", possibleTemplateName));
            return;
        }

        Collection<BuildableItem> changed = filter(
                transform(
                        filter(seedJobReferences, new SeedReferenceDigestPredicate(digest)),
                        new LookupProjectFunction()),
                Predicates.notNull());

        for (BuildableItem seedProject : changed) {
            seedProject.scheduleBuild(30, new TemplateTriggerCause());
        }
    }

    public static class TemplateTriggerCause extends Cause {
        TemplateTriggerCause() {
        }

        @Override
        public String getShortDescription() {
            return "Template has changed";
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof TemplateTriggerCause;
        }

        @Override
        public int hashCode() {
            return 3;
        }
    }

    private static class LookupProjectFunction implements Function<SeedReference, BuildableItem> {
        @Override
        public BuildableItem apply(SeedReference input) {
            return (BuildableItem) Jenkins.getInstance().getItem(input.getSeedJobName());
        }
    }
}
