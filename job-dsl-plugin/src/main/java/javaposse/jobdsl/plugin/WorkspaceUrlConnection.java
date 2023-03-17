package javaposse.jobdsl.plugin;

import hudson.FilePath;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

@Restricted(NoExternalUse.class)
public final class WorkspaceUrlConnection extends URLConnection {
    private final FilePath workspace;
    private InputStream is;

    public WorkspaceUrlConnection(URL url, FilePath workspace) {
        super(url);
        this.workspace = workspace;
    }

    @Override
    public void connect() throws IOException {
        String relativePath = url.getFile().substring(1); // remove leading slash
        FilePath targetPath = workspace.child(relativePath);
        try {
            if (!targetPath.exists()) {
                throw new FileNotFoundException("Unable to find file at " + targetPath);
            }
            is = targetPath.read();
            connected = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }
        return is;
    }

    @Override
    public String getContentType() {
        return guessContentTypeFromName(url.getFile());
    }
}
