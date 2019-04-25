package javaposse.jobdsl.plugin.casc;

import hudson.ExtensionPoint;
import hudson.model.AbstractDescribableImpl;

import java.io.IOException;

public abstract class ScriptSource extends AbstractDescribableImpl<ScriptSource> implements ExtensionPoint {

    public abstract String getScript() throws IOException;
}
