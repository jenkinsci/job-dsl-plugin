package javaposse.jobdsl.plugin.actions

import hudson.Util
import hudson.model.Action
import hudson.model.Item
import hudson.model.Items
import javaposse.jobdsl.plugin.SeedReference
import jenkins.model.Jenkins

class SeedJobAction implements Action {
    private final SeedReference seedReference

    final String iconFileName = null
    final String displayName = null
    final String urlName = null
    final Item item

    SeedJobAction(Item item, SeedReference seedReference) {
        this.item = item
        this.seedReference = seedReference
    }

    Item getSeedJob() {
        Jenkins.instance.getItemByFullName(seedReference.seedJobName)
    }

    Item getTemplateJob() {
        String templateJobName = seedReference.templateJobName
        templateJobName == null ? null : Jenkins.instance.getItemByFullName(templateJobName)
    }

    String getDigest() {
        seedReference.digest
    }

    boolean isConfigChanged() {
        try {
            String fileDigest = Util.getDigestOf(Items.getConfigFile(item).file)
            fileDigest != seedReference.digest
        } catch (IOException ignore) {
            false
        }
    }
}
