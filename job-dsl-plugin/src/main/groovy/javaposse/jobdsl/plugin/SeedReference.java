package javaposse.jobdsl.plugin;

/**
 * Bean to record a reference from a
 */
public class SeedReference {
    String templateJobName;
    String seedJobName;
    String digest;

    public SeedReference(String templateJobName, String seedJobName, String digest) {
        this.templateJobName = templateJobName;
        this.seedJobName = seedJobName;
        this.digest = digest;
    }

    public String getTemplateJobName() {
        return templateJobName;
    }

    public void setTemplateJobName(String templateJobName) {
        this.templateJobName = templateJobName;
    }

    public String getSeedJobName() {
        return seedJobName;
    }

    public void setSeedJobName(String seedJobName) {
        this.seedJobName = seedJobName;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
