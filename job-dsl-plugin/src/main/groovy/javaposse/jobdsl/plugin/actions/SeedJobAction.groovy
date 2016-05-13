package javaposse.jobdsl.plugin.actions;

import hudson.Util;
import hudson.model.Action;
import hudson.model.Item;
import hudson.model.Items;
import javaposse.jobdsl.plugin.SeedReference;
import jenkins.model.Jenkins;

import java.io.IOException;

public class SeedJobAction implements Action {
    private final Item item;
    private final SeedReference seedReference;

    public SeedJobAction(Item item, SeedReference seedReference) {
        this.item = item;
        this.seedReference = seedReference;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }

    public Item getItem() {
        return item;
    }

    public Item getSeedJob() {
        return Jenkins.getInstance().getItemByFullName(seedReference.getSeedJobName());
    }

    public Item getTemplateJob() {
        String templateJobName = seedReference.getTemplateJobName();
        return templateJobName == null ? null :
                Jenkins.getInstance().getItemByFullName(templateJobName);
    }

    public String getDigest() {
        return seedReference.getDigest();
    }

    public boolean isConfigChanged() {
        try {
            String fileDigest = Util.getDigestOf(Items.getConfigFile(item).getFile());
            return !fileDigest.equals(seedReference.getDigest());
        } catch (IOException e) {
            return false;
        }
    }
}
