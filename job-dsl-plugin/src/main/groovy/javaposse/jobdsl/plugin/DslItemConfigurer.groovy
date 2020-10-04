package javaposse.jobdsl.plugin

import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty
import hudson.model.Items
import javaposse.jobdsl.dsl.Item

class DslItemConfigurer {
    private static final String XML_HEADER = "<?xml version='1.1' encoding='UTF-8'?>"

    /**
     * Merge an {@link AbstractFolderProperty} into a new {@link Item}'s properties.
     *
     * @param item the property to merge
     * @param dslItem the DSL item to merge the properties into
     */
    static void mergeCredentials(AbstractFolderProperty<?> property, Item dslItem) {
        String xml = Items.XSTREAM2.toXML(property)
        Node node = new XmlParser().parseText(XML_HEADER + xml)
        dslItem.configure { p -> p / 'properties' << node }
    }
}
