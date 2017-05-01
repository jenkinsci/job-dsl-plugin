package javaposse.jobdsl

import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.FileJobManagement
import javaposse.jobdsl.dsl.GeneratedItems
import javaposse.jobdsl.dsl.GeneratedJob
import javaposse.jobdsl.dsl.GeneratedView
import javaposse.jobdsl.dsl.ScriptRequest

import java.util.logging.Logger

/**
 * Able to run from the command line to test out. Leverage FileJobManagement
 */
class Run {
    private static final Logger LOG = Logger.getLogger(Run.name)

    @SuppressWarnings('NoDef')
    static void main(String[] args) throws Exception {
        boolean scriptClasspath
        String[] files = args
        if (files.length > 0 && files[0] == '-j') {
            files = files[1..-1]
            scriptClasspath = true
        }

        if (files.length == 0) {
            LOG.severe('Script name is required')
            return
        }

        File cwd = new File('.')
        URL cwdURL = cwd.toURI().toURL()

        FileJobManagement jm = new FileJobManagement(cwd)
        jm.parameters.putAll(System.getenv())
        System.properties.each { def key, def value ->
            jm.parameters.put(key.toString(), value.toString())
        }

        files.each { String scriptName ->
            File scriptFile = new File(scriptName)
            String scriptBody = scriptFile.getText('UTF-8')
            URL urlRoot = scriptClasspath ? scriptFile.absoluteFile.parentFile.toURI().toURL() : cwdURL
            ScriptRequest request = new ScriptRequest(scriptBody, urlRoot, false, scriptFile.absolutePath)
            GeneratedItems generatedItems = new DslScriptLoader(jm).runScripts([request])

            for (GeneratedJob job : generatedItems.jobs) {
                LOG.info("From $scriptName, Generated item: $job")
            }
            for (GeneratedView view : generatedItems.views) {
                LOG.info("From $scriptName, Generated view: $view")
            }
        }
    }
}
