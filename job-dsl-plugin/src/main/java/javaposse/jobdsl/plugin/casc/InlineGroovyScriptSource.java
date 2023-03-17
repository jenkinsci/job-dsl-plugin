package javaposse.jobdsl.plugin.casc;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.casc.Configurable;
import org.jenkinsci.Symbol;

import java.io.IOException;

public class InlineGroovyScriptSource extends ConfigurableScriptSource implements Configurable {

    public String script;

    @Override
    public void configure(String script) {
        this.script = script;
    }

    @Override
    public String getScript() {
        return script;
    }

    @Extension(optional = true)
    @Symbol("script")
    public static class DescriptorImpl extends Descriptor<ScriptSource> {

    }
}
