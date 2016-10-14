package javaposse.jobdsl.plugin.actions

import groovy.transform.PackageScope
import hudson.model.Item
import hudson.model.View

@PackageScope
class Comparators {
    static final Comparator<Item> ITEM_COMPARATOR = new Comparator<Item>() {
        @Override
        int compare(Item o1, Item o2) {
            o1.fullDisplayName <=> o2.fullDisplayName
        }
    }

    static final Comparator<View> VIEW_COMPARATOR = new Comparator<View>() {
        @Override
        int compare(View o1, View o2) {
            getFullDisplayName(o1) <=> getFullDisplayName(o2)
        }

        private static String getFullDisplayName(View view) {
            String ownerDisplayName = view.ownerItemGroup.fullDisplayName
            ownerDisplayName.length() == 0 ? view.displayName : "${ownerDisplayName} » ${view.displayName}"
        }
    }
}
