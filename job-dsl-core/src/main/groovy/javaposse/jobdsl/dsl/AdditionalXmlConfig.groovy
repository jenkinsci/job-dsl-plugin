package javaposse.jobdsl.dsl

import groovy.util.Node

public abstract class AdditionalXmlConfig extends XmlConfig {
    
    public AdditionalXmlConfig(XmlConfigType configType) {
        super(configType)
    }

    /**
     * The path of the xml - relative to job main config.
     * @return Path
     */
    protected abstract String getRelativePath();
    
}
