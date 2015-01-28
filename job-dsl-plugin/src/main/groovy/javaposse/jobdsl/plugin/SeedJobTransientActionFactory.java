package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.AbstractItem;
import hudson.model.Action;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

import static java.util.Collections.emptyList;

@Extension
public class SeedJobTransientActionFactory extends TransientActionFactory<AbstractItem> {
    @Override
    public Class<AbstractItem> type() {
        return AbstractItem.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull AbstractItem target) {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        SeedReference seedReference = descriptor.getGeneratedJobMap().get(target.getFullName());
        if (seedReference != null) {
            return Arrays.asList(new SeedJobAction(target, seedReference));
        } else {
            return emptyList();
        }
    }
}
