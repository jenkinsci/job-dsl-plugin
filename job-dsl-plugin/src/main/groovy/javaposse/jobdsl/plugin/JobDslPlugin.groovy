package javaposse.jobdsl.plugin

import hudson.Plugin
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse

import javax.servlet.ServletException

@Deprecated
class JobDslPlugin extends Plugin {
    @SuppressWarnings(['deprecation', 'UnnecessaryConstructor'])
    JobDslPlugin() {
        super()
    }

    @Override
    void doDynamic(StaplerRequest request, StaplerResponse response) throws IOException, ServletException {
        String path = request.restOfPath
        if (path == '/api-viewer') {
            response.sendRedirect("${request.requestURI}${request.requestURI.endsWith('/') ? '' : '/'}index.html")
        } else {
            super.doDynamic(request, response)
        }
    }
}
