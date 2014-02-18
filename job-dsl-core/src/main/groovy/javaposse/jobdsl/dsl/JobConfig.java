package javaposse.jobdsl.dsl;

import java.util.HashMap;
import java.util.Map;

public class JobConfig {

    public static final JobConfigId MAIN_CONFIG_ID = new JobConfigId(XmlConfigType.JOB, null);

    private Map<JobConfigId, String> configs = new HashMap<JobConfigId, String>();

    public void setMainConfig(String config) {
        configs.put(MAIN_CONFIG_ID, config);
    }

    public String getMainConfig() {
        return configs.get(MAIN_CONFIG_ID);
    }
    
    public void addConfig(JobConfigId id, String config) {
        configs.put(id, config);
    }

    public String getConfig(JobConfigId id) {
        return configs.get(id);
    }

    public Map<JobConfigId, String> getConfigs() {
        return configs;
    }

    public boolean isValid() {
        for (JobConfigId type : configs.keySet()) {
            if (configs.get(type) == null || configs.get(type).isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
