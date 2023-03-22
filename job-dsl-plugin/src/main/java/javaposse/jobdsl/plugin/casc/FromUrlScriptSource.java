package javaposse.jobdsl.plugin.casc;

import hudson.Extension;
import hudson.model.Descriptor;
import io.jenkins.plugins.casc.Configurable;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.Symbol;

import java.io.IOException;
import java.net.URI;

public class FromUrlScriptSource extends ConfigurableScriptSource implements Configurable {

    public String url;

    @Override
    public void configure(String url) {
        this.url = url;
    }

    @Override
    public String getScript() throws IOException {
        return IOUtils.toString(URI.create(url));
    }

    @Extension(optional = true)
    @Symbol("url")
    public static class DescriptorImpl extends Descriptor<ScriptSource> {

    }
}
