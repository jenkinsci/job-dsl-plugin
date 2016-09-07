package javaposse.jobdsl.plugin

import hudson.ExtensionList
import hudson.ExtensionListListener
import hudson.Plugin
import hudson.model.Descriptor
import jenkins.model.Jenkins
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse

import javax.servlet.ServletException

class JobDslPlugin extends Plugin {
    private volatile String cachedApi

    @Override
    void postInitialize() throws Exception {
        ExtensionList.lookup(Descriptor).addListener(new ExtensionListListener() {
            @Override
            void onChange() {
                cachedApi = null
            }
        })
    }

    @Override
    void doDynamic(StaplerRequest request, StaplerResponse response) throws IOException, ServletException {
        String path = request.restOfPath
        if (path == '/api-viewer') {
            response.sendRedirect("${request.requestURI}${request.requestURI.endsWith('/') ? '' : '/'}index.html")
        } else if (path == '/api-viewer/build/data/update-center.jsonp') {
            response.contentType = 'application/javascript'
            response.writer.print(generateUpdateCenter())
        } else if (path == '/api-viewer/build/data/dsl.json') {
            response.contentType = 'application/json'
            response.writer.print(generateApi())
        } else {
            super.doDynamic(request, response)
        }
    }

    private String generateApi() {
        String api = cachedApi
        if (api == null) {
            api = new EmbeddedApiDocGenerator().generateApi()
            cachedApi = api
        }
        api
    }

    private static String generateUpdateCenter() {
        Map<String, Object> plugins = [:]
        Jenkins.instance.updateCenter.sites.each {
            plugins.putAll(it.JSONObject.getJSONObject('plugins'))
        }

        JSONObject data = new JSONObject()
        data['plugins'] = plugins

        "updateCenter.post(${data.toString()})"
    }
}
