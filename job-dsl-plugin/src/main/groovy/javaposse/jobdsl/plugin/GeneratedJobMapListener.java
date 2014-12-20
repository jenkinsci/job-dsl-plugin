package javaposse.jobdsl.plugin;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.listeners.ItemListener;
import jenkins.model.Jenkins;

@Extension
public class GeneratedJobMapListener extends ItemListener {

    @Override
    public void onDeleted(Item item) {
        DescriptorImpl descriptor = Jenkins.getInstance().getDescriptorByType(DescriptorImpl.class);
        SeedReference seedReference = descriptor.getGeneratedJobMap().remove(item.getFullName());
        if (seedReference != null) {
            descriptor.save();
        }
    }
}
