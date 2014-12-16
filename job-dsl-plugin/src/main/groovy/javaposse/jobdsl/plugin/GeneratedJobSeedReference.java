package javaposse.jobdsl.plugin;

public class GeneratedJobSeedReference extends SeedReference {

    String generatedJobName;

    public GeneratedJobSeedReference(String generatedJobName, String seedJobName, String digest) {
        super(null, seedJobName, digest);
        this.generatedJobName = generatedJobName;
    }

    public String getGeneratedJobName() {
        return generatedJobName;
    }

    public void setGeneratedJobName(String generatedJobName) {
        this.generatedJobName = generatedJobName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeneratedJobSeedReference that = (GeneratedJobSeedReference) o;

        if (generatedJobName != null ? !generatedJobName.equals(that.generatedJobName) : that.generatedJobName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (generatedJobName != null ? generatedJobName.hashCode() : 0);
        return result;
    }
}
