package javaposse.jobdsl.plugin.casc;

import hudson.Extension;
import hudson.model.Descriptor;
import org.apache.commons.io.FileUtils;
import org.jenkinsci.Symbol;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FromFileScriptSource extends ConfigurableScriptSource {

    public String path;

    @Override
    public void configure(String path) {
        this.path = path;
    }

    @Override
    public String getScript() throws IOException {
        return FileUtils.readFileToString(new File(path), StandardCharsets.UTF_8);
    }

    @Extension(optional = true)
    @Symbol("file")
    public static class DescriptorImpl extends Descriptor<ScriptSource> {

    }
}
