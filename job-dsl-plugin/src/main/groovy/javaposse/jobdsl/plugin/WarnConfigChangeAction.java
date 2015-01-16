package javaposse.jobdsl.plugin;

import hudson.Util;
import hudson.model.AbstractItem;
import hudson.model.Action;

import java.io.IOException;
import java.util.logging.Logger;

public class WarnConfigChangeAction implements Action {

    private static final Logger LOGGER = Logger.getLogger(WarnConfigChangeAction.class.getName());
    private AbstractItem item;
    private String digest;

    public WarnConfigChangeAction(AbstractItem item, String digest) {
        this.item = item;
        this.digest = digest;
    }

    public boolean isConfigChanged() {
        try {
            String fileDigest = Util.getDigestOf(item.getConfigFile().getFile());
            return !fileDigest.equals(digest);
        } catch (IOException e) {
            LOGGER.warning("Unable to generate file digest, not warning user");
            return false;
        }
    }

    public AbstractItem getItem() {
        return item;
    }

    public String getDigest() {
        return digest;
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
        return "warnConfigChange";
    }
}
