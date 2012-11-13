package sun.net.www.protocol.workspace;

import hudson.FilePath;
import hudson.model.AbstractProject;
import jenkins.model.Jenkins;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WorkspaceConnection extends URLConnection {
    InputStream is;

    public WorkspaceConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        String jobName = url.getHost();
        Jenkins jenkins = Jenkins.getInstance();
        if(jenkins == null) {
            throw new IllegalStateException("Not in a running Jenkins");
        }
        AbstractProject project = (AbstractProject) jenkins.getItem(jobName);
        FilePath workspace = project.getSomeWorkspace();

        String path = url.getFile();
        String relativePath = path.substring(1, path.length());
        FilePath targetPath = workspace.child(relativePath);

        // Make sure we can find the file
        try {
            if (!targetPath.exists()) {
                throw new FileNotFoundException("Unable to find file at " + path);
            }
        } catch (InterruptedException e) {
            throw new IOException(e);
        }

        is = targetPath.read();
        connected = true;
    }

    @Override
    synchronized public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }
        return ( is );
    }

    public String getContentType() {
        return guessContentTypeFromName( url.getFile() );
    }
}
