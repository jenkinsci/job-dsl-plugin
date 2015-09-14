package javaposse.jobdsl.plugin

import hudson.FilePath

class WorkspaceUrlConnection extends URLConnection {
    InputStream is

    WorkspaceUrlConnection(URL url) {
        super(url)
    }

    @Override
    void connect() throws IOException {
        FilePath targetPath = WorkspaceProtocol.getFilePathFromUrl(url)

        // Make sure we can find the file
        try {
            if (!targetPath.exists()) {
                throw new FileNotFoundException("Unable to find file at ${targetPath}")
            }

            is = targetPath.read()
            connected = true
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt()
            throw new IOException(e)
        }
    }

    @Override
    InputStream getInputStream() throws IOException {
        if (!connected) {
            connect()
        }
        is
    }

    String getContentType() {
        guessContentTypeFromName(url.file)
    }
}
