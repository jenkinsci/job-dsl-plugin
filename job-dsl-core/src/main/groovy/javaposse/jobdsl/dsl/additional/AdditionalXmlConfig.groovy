package javaposse.jobdsl.dsl.additional

import javaposse.jobdsl.dsl.ItemType
import javaposse.jobdsl.dsl.Item

abstract class AdditionalXmlConfig extends Item {

    protected AdditionalXmlConfig() {
        super(ItemType.ADDITIONAL)
    }

    /**
     * The path of the additional xml - relative to job main config.
     * @return Path
     */
    abstract String getRelativePath()
}
