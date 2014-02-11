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

    boolean createOrUpdateConfig(String jobName, String config, Map<String, String> configPromotions, boolean ignoreExisting)
        throws JobNameNotProvidedException, JobConfigurationMissingException {
        validateUpdateArgs(jobName, config);

        new File(jobName + ext).write(config)
        
        for (String promotionName : configPromotions.keySet()) {
            new File(promotionName + ext).write(configPromotions.get(promotionName))
        }
        
        return true
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

