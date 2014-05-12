package javaposse.jobdsl.plugin;

import hudson.model.Item;
import hudson.model.listeners.ItemListener;

import java.util.logging.Logger;

public class SeedJobItemListener extends ItemListener {
    private static final Logger LOGGER = Logger.getLogger(SeedJobItemListener.class.getName());

    @Override
    public void onCreated(Item item) {
        LOGGER.info("onCreated");
    }

    @Override
    public void onLoaded() {
        LOGGER.info("onLoaded");
    }

    @Override
    public void onDeleted(Item item) {
        LOGGER.info("onDeleted");
    }

    // 1.460
//    @Override
//    public void onUpdated(Item item) {
//        LOGGER.info("onDeleted");
//    }

    @Override
    public void onRenamed(Item item, String oldName, String newName) {
        LOGGER.info("onRenamed");
        super.onRenamed(item, oldName, newName);
    }


}
