package javaposse.jobdsl.plugin;

import com.cloudbees.hudson.plugins.folder.AbstractFolder;
import com.cloudbees.hudson.plugins.folder.AbstractFolderProperty;
import com.cloudbees.hudson.plugins.folder.AbstractFolderPropertyDescriptor;
import hudson.Extension;

public class FakeProperty extends AbstractFolderProperty<AbstractFolder<?>> {
    @Extension(optional = true)
    public static class DescriptorImpl extends AbstractFolderPropertyDescriptor {
        @Override
        public String getDisplayName() {
            return "";
        }
    }
}
