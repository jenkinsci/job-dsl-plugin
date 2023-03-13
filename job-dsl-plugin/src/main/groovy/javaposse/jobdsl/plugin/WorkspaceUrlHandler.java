package javaposse.jobdsl.plugin;

import hudson.FilePath;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
public final class WorkspaceUrlHandler extends URLStreamHandler {
    private final FilePath workspace;

    public WorkspaceUrlHandler(FilePath workspace) {
        this.workspace = workspace;
    }

    @Override
    protected URLConnection openConnection(URL url) {
        return new WorkspaceUrlConnection(url, workspace);
    }
}
