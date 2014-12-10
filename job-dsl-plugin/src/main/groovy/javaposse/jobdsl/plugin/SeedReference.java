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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SeedReference that = (SeedReference) o;

        if (digest != null ? !digest.equals(that.digest) : that.digest != null) return false;
        if (seedJobName != null ? !seedJobName.equals(that.seedJobName) : that.seedJobName != null) return false;
        if (templateJobName != null ? !templateJobName.equals(that.templateJobName) : that.templateJobName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = templateJobName != null ? templateJobName.hashCode() : 0;
        result = 31 * result + (seedJobName != null ? seedJobName.hashCode() : 0);
        result = 31 * result + (digest != null ? digest.hashCode() : 0);
        return result;
    }
}
