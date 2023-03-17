package javaposse.jobdsl.plugin.casc;

import io.jenkins.plugins.casc.Configurable;
import io.jenkins.plugins.casc.ConfiguratorException;
import io.jenkins.plugins.casc.model.CNode;

public abstract class ConfigurableScriptSource extends ScriptSource implements Configurable {

    @Override
    public void configure(CNode node) throws ConfiguratorException {
        configure(node.asScalar().getValue());
    }

    protected abstract void configure(String value);

    @Override
    public void check(CNode node) throws ConfiguratorException {
        node.asScalar();
    }

    @Override
    public CNode describe() {
        return null; // Not relevant here
    }

}
