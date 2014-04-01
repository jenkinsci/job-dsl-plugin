package javaposse.jobdsl.dsl.additional

import javaposse.jobdsl.dsl.XmlConfig
import javaposse.jobdsl.dsl.XmlConfigType

public abstract class AdditionalXmlConfig extends XmlConfig {
    
    public AdditionalXmlConfig() {
        super(XmlConfigType.ADDITIONAL)
    }

    /**
     * The path of the additional xml - relative to job main config.
     * @return Path
     */
    public abstract String getRelativePath();
    
}
