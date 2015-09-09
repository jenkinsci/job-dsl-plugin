package javaposse.jobdsl.plugin;

import hudson.FilePath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class WorkspaceUrlConnection extends URLConnection {
    InputStream is;

    public WorkspaceUrlConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        FilePath targetPath = WorkspaceProtocol.getFilePathFromUrl(url);

        // Make sure we can find the file
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
