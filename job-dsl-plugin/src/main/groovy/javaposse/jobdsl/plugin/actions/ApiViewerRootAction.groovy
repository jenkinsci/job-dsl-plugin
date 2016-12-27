package javaposse.jobdsl.plugin.actions

import hudson.Extension
import hudson.ExtensionList
import hudson.ExtensionListListener
import hudson.PluginWrapper
import hudson.model.Descriptor
import hudson.model.RootAction
import hudson.model.UpdateSite
import javaposse.jobdsl.plugin.EmbeddedApiDocGenerator
import jenkins.model.Jenkins
import net.sf.json.JSONObject
import org.kohsuke.stapler.StaplerRequest
import org.kohsuke.stapler.StaplerResponse

import javax.servlet.ServletException

@Extension
class ApiViewerRootAction implements RootAction {
    private volatile CachedFile cachedData
    private volatile CachedFile cachedPlugins

    final String iconFileName = null
    final String displayName = null
    final String urlName = 'job-dsl-api-viewer'

    ApiViewerRootAction() {
        ExtensionList.lookup(Descriptor).addListener(new ExtensionListListener() {
            @Override
            void onChange() {
                cachedData = null
            }
        })
    }

    @SuppressWarnings('GroovyUnusedDeclaration')
    void doPlugins(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
        serveCachedFile(request, response, generatePlugins(), 'update-center.json')
    }

    @SuppressWarnings('GroovyUnusedDeclaration')
    void doData(StaplerRequest request, StaplerResponse response) throws ServletException, IOException {
        serveCachedFile(request, response, generateData(), 'dsl.json')
    }

    private CachedFile generateData() {
        CachedFile data = cachedData
        if (data == null) {
            data = new CachedFile(new EmbeddedApiDocGenerator().generateApi(), System.currentTimeMillis())
            cachedData = data
        }
        data
    }

    private CachedFile generatePlugins() {
        Collection<UpdateSite> sites = Jenkins.instance.updateCenter.sites.findAll { it.JSONObject != null }
        long lastModified = sites*.dataTimestamp.max() ?: 0
        CachedFile updateCenter = cachedPlugins
        if (updateCenter == null || lastModified > updateCenter.timestamp) {
            Map<String, Object> plugins = [:]
            sites.each {
                plugins.putAll(it.JSONObject.getJSONObject('plugins'))
            }

            // remove unused keys
            plugins.values().each { JSONObject plugin ->
                Set keys = new HashSet(plugin.keySet())
                keys.removeAll('name', 'title', 'wiki', 'excerpt')
                keys.each { plugin.remove(it) }
            }

            // add plugins that are not available in the Update Center
            jenkins.model.Jenkins.instance.pluginManager.plugins.each { PluginWrapper plugin ->
                if (!plugins.containsKey(plugin.shortName)) {
                    JSONObject pluginJson = new JSONObject()
                    pluginJson.put('name', plugin.shortName)
                    pluginJson.put('title', plugin.displayName)
                    if (plugin.url) {
                        pluginJson.put('wiki', plugin.url)
                    }
                    plugins.put(plugin.shortName, pluginJson)
                }
            }

            JSONObject data = new JSONObject()
            data['plugins'] = plugins

            updateCenter = new CachedFile(data.toString(), lastModified)
            cachedPlugins = updateCenter
        }
        updateCenter
    }

    private static serveCachedFile(StaplerRequest request, StaplerResponse response, CachedFile file, String fileName) {
        response.serveFile(
                request,
                new ByteArrayInputStream(file.data),
                file.timestamp,
                0,
                file.data.length as long,
                fileName
        )
    }

    private static class CachedFile {
        final byte[] data
        final long timestamp

        CachedFile(String data, long timestamp) {
            this.data = data.getBytes('UTF-8')
            this.timestamp = timestamp
        }
    }
}
