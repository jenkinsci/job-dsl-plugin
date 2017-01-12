package javaposse.jobdsl.plugin;

import javaposse.jobdsl.dsl.AbstractDslScriptLoader;
import javaposse.jobdsl.dsl.GeneratedConfigFile;
import javaposse.jobdsl.dsl.GeneratedItems;
import javaposse.jobdsl.dsl.JobManagement;
import javaposse.jobdsl.dsl.ScriptRequest;
import jenkins.model.Jenkins;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.jenkinsci.lib.configprovider.model.Config;
import org.jenkinsci.plugins.configfiles.GlobalConfigFiles;

/**
 * @since 1.58
 */
public class JenkinsDslScriptLoader extends AbstractDslScriptLoader<JenkinsJobParent, GeneratedItems> {
    public JenkinsDslScriptLoader(JobManagement jobManagement) {
        super(jobManagement, JenkinsJobParent.class, GeneratedItems.class);
    }

    @Override
    protected void extractGeneratedItems(GeneratedItems generatedItems, JenkinsJobParent jobParent, ScriptRequest scriptRequest) {
        super.extractGeneratedItems(generatedItems, jobParent, scriptRequest);

        if (DefaultGroovyMethods.asBoolean(Jenkins.getInstance().getPluginManager().getPlugin("config-file-provider"))) {
            GlobalConfigFiles globalConfigFiles = GlobalConfigFiles.get();
            for (Object o : jobParent.getReferencedConfigs()) {
                Config config = (Config) o;
                if (!(scriptRequest.getIgnoreExisting() && globalConfigFiles.getById(config.id) != null)) {
                    globalConfigFiles.save(config);
                }
                generatedItems.getConfigFiles().add(new GeneratedConfigFile(config.id, config.name));
            }
        }
    }
}
