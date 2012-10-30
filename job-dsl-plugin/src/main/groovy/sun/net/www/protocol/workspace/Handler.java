package sun.net.www.protocol.workspace;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * workspace://JOB-NAME/
 *
 * Don't need a URLStreamHandlerFactory because of the package name we're using.
 */
public class Handler extends URLStreamHandler {

    protected URLConnection openConnection(URL url) throws IOException {
        return new WorkspaceConnection( url );
    }
}
