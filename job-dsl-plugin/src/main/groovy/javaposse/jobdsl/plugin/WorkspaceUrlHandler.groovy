package javaposse.jobdsl.plugin

/**
 * workspace://JOB-NAME/
 *
 * Don't need a URLStreamHandlerFactory because of the package name we're using.
 */
class WorkspaceUrlHandler extends URLStreamHandler {

    protected URLConnection openConnection(URL url) throws IOException {
        return new WorkspaceUrlConnection( url )
    }

    /**
     * Real hacky. Essentially, when URL does a lookup for  handler class it uses it's own classloader, and we're loaded
     * into a special plugin classloader, hence URL can't see this class. This forces it into the GLOBAL url class's
     * list of handlers. Do what ever possible to avoid this.
     * @return
     */
    static installHandler() {
        Map<String, URLStreamHandler> handlers = URL.handlers
        if (!handlers.containsKey('workspace')) {
            URL.handlers.put('workspace', new WorkspaceUrlHandler())
        }
    }
}
