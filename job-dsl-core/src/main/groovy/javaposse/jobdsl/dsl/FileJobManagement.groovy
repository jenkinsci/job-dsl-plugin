package javaposse.jobdsl.dsl

import com.google.common.collect.Maps

class FileJobManagement extends AbstractJobManagement {
    /**
     * Root of where to look for job config files
     */
    File root

    /**
     * Extension to append to job name when looking at the filesystem
     */
    String ext

    /**
     * map to store job parameters from System properties and
     * Environment variables.
     */
    protected Map params =  Maps.newHashMap();

    public FileJobManagement(File root, String ext = null, PrintStream out = System.out) {
        super(out)
        this.root = root
        this.ext = ext?:".xml"
    }

    String getConfig(String jobName) throws JobConfigurationNotFoundException {

        if (jobName.isEmpty()) return '''
<project>
  <actions/>
  <description/>
  <keepDependencies>false</keepDependencies>
  <properties/>
</project>'''

        try {
            new File(root, jobName + ext).getText()
        } catch (IOException ioex) {
            throw new JobConfigurationNotFoundException(jobName)
        }
    }
    
    @Override
    boolean createOrUpdateConfig(String jobName, JobConfig config, boolean ignoreExisting)
        throws NameNotProvidedException, ConfigurationMissingException {
        validateUpdateArgs(jobName, config);

        new File(jobName + ext).write(config.getMainConfig())
        
        for (JobConfigId configId : config.configs.keySet()) {
            new File(configId.getType().toString() + configId.getRelativePath() + jobName + ext).write(config.getConfig(configId))
        }
        
        return true
    }

    @Override
    void createOrUpdateView(String viewName, String config, boolean ignoreExisting) {
        validateUpdateArgs(viewName, config);

        new File(viewName + ext).write(config)
    }

    @Override
    public Map<String, String> getParameters() {
        return params;
    }

    @Override
    public InputStream streamFileInWorkspace(String filePath) {
        return new FileInputStream(new File(root, filePath));
    }

    @Override
    public String readFileInWorkspace(String filePath) {
        new File(root, filePath).text
    }
}

