package javaposse.jobdsl.plugin

/**
 * workspace://JOB-NAME/
 *
 * Don't need a URLStreamHandlerFactory because of the package name we're using.
 */
@Deprecated
class WorkspaceUrlHandler extends URLStreamHandler {
    protected URLConnection openConnection(URL url) throws IOException {
        new WorkspaceUrlConnection(url)
    }
}
