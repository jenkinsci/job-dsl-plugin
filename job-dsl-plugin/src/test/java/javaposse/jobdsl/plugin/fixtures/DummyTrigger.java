package javaposse.jobdsl.plugin.fixtures;

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.triggers.Trigger;
import hudson.triggers.TriggerDescriptor;
import java.util.List;
import jenkins.mvn.SettingsProvider;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class DummyTrigger extends Trigger<Job> {
    @DataBoundConstructor
    public DummyTrigger() {}

    public String getaString() {
        return aString;
    }

    public void setaString(String aString) {
        this.aString = aString;
    }

    public Integer getAnInteger() {
        return anInteger;
    }

    public void setAnInteger(Integer anInteger) {
        this.anInteger = anInteger;
    }

    public boolean getaBoolean() {
        return aBoolean;
    }

    public boolean isaBoolean() {
        return aBoolean;
    }

    public void setaBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
    }

    public Thread.State getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(Thread.State anEnum) {
        this.anEnum = anEnum;
    }

    public SettingsProvider getaHeterogeneous() {
        return aHeterogeneous;
    }

    public void setaHeterogeneous(SettingsProvider aHeterogeneous) {
        this.aHeterogeneous = aHeterogeneous;
    }

    public List<SettingsProvider> getaHeterogeneousList() {
        return aHeterogeneousList;
    }

    public void setaHeterogeneousList(List<SettingsProvider> aHeterogeneousList) {
        this.aHeterogeneousList = aHeterogeneousList;
    }

    public ADescribable getaHomogeneous() {
        return aHomogeneous;
    }

    public void setaHomogeneous(ADescribable aHomogeneous) {
        this.aHomogeneous = aHomogeneous;
    }

    public List<ADescribable> getaHomogeneousList() {
        return aHomogeneousList;
    }

    public void setaHomogeneousList(List<ADescribable> aHomogeneousList) {
        this.aHomogeneousList = aHomogeneousList;
    }

    public ABean getaHomogeneousBean() {
        return aHomogeneousBean;
    }

    public void setaHomogeneousBean(ABean aHomogeneousBean) {
        this.aHomogeneousBean = aHomogeneousBean;
    }

    public List<ABean> getaHomogeneousBeanList() {
        return aHomogeneousBeanList;
    }

    public void setaHomogeneousBeanList(List<ABean> aHomogeneousBeanList) {
        this.aHomogeneousBeanList = aHomogeneousBeanList;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public List<Thread.State> getEnumList() {
        return enumList;
    }

    public void setEnumList(List<Thread.State> enumList) {
        this.enumList = enumList;
    }

    public UnsupportedByStructs getUnsupportedByStructs() {
        return unsupportedByStructs;
    }

    public void setUnsupportedByStructs(UnsupportedByStructs unsupportedByStructs) {
        this.unsupportedByStructs = unsupportedByStructs;
    }

    @DataBoundSetter private String aString;
    @DataBoundSetter private Integer anInteger;
    @DataBoundSetter private boolean aBoolean;
    @DataBoundSetter private Thread.State anEnum;
    @DataBoundSetter private SettingsProvider aHeterogeneous;
    @DataBoundSetter private List<SettingsProvider> aHeterogeneousList;
    @DataBoundSetter private ADescribable aHomogeneous;
    @DataBoundSetter private List<ADescribable> aHomogeneousList;
    @DataBoundSetter private ABean aHomogeneousBean;
    @DataBoundSetter private List<ABean> aHomogeneousBeanList;
    @DataBoundSetter private List<String> stringList;
    @DataBoundSetter private List<Thread.State> enumList;
    @DataBoundSetter private UnsupportedByStructs unsupportedByStructs;

    public static class UnsupportedByStructs {}

    @Extension
    @Symbol("dummy")
    public static class DescriptorImpl extends TriggerDescriptor {
        @Override
        public boolean isApplicable(Item item) {
            return false;
        }

        @Override
        public final String getDisplayName() {
            return "";
        }
    }
}
