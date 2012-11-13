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

    /**
     * Real hacky. Essentially, when URL does a lookup for  handler class it uses it's own classloader, and we're loaded
     * into a special plugin classloader, hence URL can't see this class. This forces it into the GLOBAL url class's
     * list of handlers. Do what ever possible to avoid this.
     * @return
     */
    def static installHandler() {
        Map<String, URLStreamHandler> handlers = URL.handlers;
        if( !handlers.containsKey('workspace') )
        URL.handlers.put('workspace', new Handler());
    }
}
