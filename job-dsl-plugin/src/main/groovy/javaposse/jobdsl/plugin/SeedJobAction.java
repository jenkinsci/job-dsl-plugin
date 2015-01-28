package javaposse.jobdsl.plugin;

import hudson.Util;
import hudson.model.AbstractItem;
import hudson.model.Action;
import hudson.model.Item;
import jenkins.model.Jenkins;

import java.io.IOException;
import java.util.logging.Logger;

public class SeedJobAction implements Action {
    private static final Logger LOGGER = Logger.getLogger(SeedJobAction.class.getName());

    private final AbstractItem item;
    private final SeedReference seedReference;

    public SeedJobAction(AbstractItem item, SeedReference seedReference) {
        this.item = item;
        this.seedReference = seedReference;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Seed job:";
    }

    @Override
    public String getUrlName() {
        return "seedJob";
    }

    public AbstractItem getItem() {
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
            String fileDigest = Util.getDigestOf(item.getConfigFile().getFile());
            return !fileDigest.equals(seedReference.getDigest());
        } catch (IOException e) {
            LOGGER.warning("Unable to generate file digest, not warning user");
            return false;
        }
    }
}
