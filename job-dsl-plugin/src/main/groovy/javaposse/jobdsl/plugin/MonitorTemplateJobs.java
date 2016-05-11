package javaposse.jobdsl.plugin;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import hudson.Extension;
import hudson.Util;
import hudson.XmlFile;
import hudson.model.AbstractProject;
import hudson.model.Cause;
import hudson.model.Saveable;
import hudson.model.listeners.SaveableListener;
import jenkins.YesNoMaybe;
import jenkins.model.Jenkins;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;

@Extension(dynamicLoadable = YesNoMaybe.YES)
public class MonitorTemplateJobs extends SaveableListener {
    private static final Logger LOGGER = Logger.getLogger(MonitorTemplateJobs.class.getName());

    @SuppressWarnings("rawtypes")
    @Override
    public void onChange(Saveable saveable, XmlFile file) {
        LOGGER.finest("onChange");

        if (!AbstractProject.class.isAssignableFrom(saveable.getClass())) {
            LOGGER.finest(String.format("%s is not a Project", saveable.getClass()));
            return;
        }

        // Look for template jobs
        AbstractProject project = (AbstractProject) saveable;
        String possibleTemplateName = project.getName();

        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        Collection<SeedReference> seedJobReferences = descriptor.getTemplateJobMap().get(possibleTemplateName);

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

        Collection<AbstractProject> changed = filter(
                transform(
                        filter(seedJobReferences, new SeedReferenceDigestPredicate(digest)),
                        new LookupProjectFunction()),
                Predicates.notNull());

        for (AbstractProject seedProject : changed) {
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

    private static class LookupProjectFunction implements Function<SeedReference, AbstractProject> {
        @Override
        public AbstractProject apply(SeedReference input) {
            return (AbstractProject) Jenkins.getInstance().getItem(input.getSeedJobName());
        }
    }
}
