package javaposse.jobdsl.dsl;


public class JobConfigId {

    private ItemType type;

    private String relativePath;

    public JobConfigId(ItemType type, String relativePath) {
        this.type = type;
        this.relativePath = relativePath;
    }

    public ItemType getType() {
        return type;
    }

    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relativePath == null) ? 0 : relativePath.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobConfigId other = (JobConfigId) obj;
        if (relativePath == null) {
            if (other.relativePath != null)
                return false;
        } else if (!relativePath.equals(other.relativePath))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
