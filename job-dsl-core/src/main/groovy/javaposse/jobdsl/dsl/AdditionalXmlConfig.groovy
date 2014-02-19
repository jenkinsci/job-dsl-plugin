package javaposse.jobdsl.dsl

import groovy.util.Node

public abstract class AdditionalXmlConfig extends XmlConfig {
    
    public AdditionalXmlConfig(XmlConfigType configType) {
        super(XmlConfigType.ADDITIONAL)
    }

    /**
     * The path of the additional xml - relative to job main config.
     * @return Path
     */
    protected abstract String getRelativePath();
    
}
