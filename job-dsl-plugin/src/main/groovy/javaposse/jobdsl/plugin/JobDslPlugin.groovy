package javaposse.jobdsl.plugin

import hudson.Plugin
import javaposse.jobdsl.dsl.JobManagement
import jenkins.model.Jenkins
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse

import javax.servlet.ServletException

import static hudson.model.UpdateCenter.ID_DEFAULT

class JobDslPlugin extends Plugin {
    private JSONObject api

    @Override
    void postInitialize() throws Exception {
        api = JSONObject.fromObject(JobManagement.getResource('dsl.json').text)
        api.element('embedded', true)
    }

    @Override
    void doDynamic(StaplerRequest request, StaplerResponse response) throws IOException, ServletException {
        String path = request.restOfPath
        if (path == '/api-viewer') {
            response.sendRedirect("${request.requestURI}/index.html")
        } else if (path == '/api-viewer/build/data/update-center.jsonp') {
            JSONObject data = Jenkins.instance.updateCenter.getById(ID_DEFAULT).JSONObject
            response.contentType = 'application/javascript'
            response.writer.print("updateCenter.post(${data.toString()})")
        } else if (path == '/api-viewer/build/data/dsl.json') {
            response.contentType = 'application/json'
            response.writer.print(api.toString())
        } else {
            super.doDynamic(request, response)
        }
    }
}
