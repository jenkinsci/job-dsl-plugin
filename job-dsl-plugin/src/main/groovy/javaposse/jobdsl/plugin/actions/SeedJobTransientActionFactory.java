package javaposse.jobdsl.plugin.actions;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Item;
import javaposse.jobdsl.plugin.DescriptorImpl;
import javaposse.jobdsl.plugin.SeedReference;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;

import javax.annotation.Nonnull;
import java.util.Collection;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Extension
public class SeedJobTransientActionFactory extends TransientActionFactory<Item> {
    @Override
    public Class<Item> type() {
        return Item.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Item target) {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        SeedReference seedReference = descriptor.getGeneratedJobMap().get(target.getFullName());
        if (seedReference != null) {
            return singletonList(new SeedJobAction(target, seedReference));
        } else {
            return emptyList();
        }
    }
}
