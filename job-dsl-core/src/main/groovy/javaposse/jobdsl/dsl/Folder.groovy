package javaposse.jobdsl.dsl

import groovy.util.Node

class Folder extends JobItem {

    private static final String FOLDER_ELEMENT_NAME = 'com.cloudbees.hudson.plugins.folder.Folder'

    Set<JobItem> referencedJobs

    public Folder(JobManagement jobManagement, Set<JobItem> referencedJobs, JobItem parent=null) {
        super(jobManagement, parent);
        this.referencedJobs = referencedJobs
    }

    public Job job(Map<String, Object> arguments=[:], Closure closure) {
        return JobParent.job(jobManagement, referencedJobs, arguments, closure, this)
    }

    public Folder folder(Closure closure) {
        return JobParent.folder(jobManagement, referencedJobs, closure, this)
    }

    @Override
    protected void matchTemplate(Node templateNode) {
        def nodeElement = templateNode.name()
        if (FOLDER_ELEMENT_NAME != nodeElement) {
            throw new JobTypeMismatchException(name, templateName);
        }
    }

    @Override
    protected String getTemplate() {
        return emptyFolderTemplate
    }

    def emptyFolderTemplate = '''<?xml version='1.0' encoding='UTF-8'?>
<com.cloudbees.hudson.plugins.folder.Folder plugin='cloudbees-folder@4.1'>
    <properties></properties>
    <icon class='com.cloudbees.hudson.plugins.folder.icons.StockFolderIcon'></icon>
    <views>
        <hudson.model.ListView>
            <owner class='com.cloudbees.hudson.plugins.folder.Folder' reference='../../..'></owner>
            <name>All</name>
            <filterExecutors>false</filterExecutors>
            <filterQueue>false</filterQueue>
            <properties class='hudson.model.View$PropertyList'></properties>
            <jobNames class='tree-set'>
                <comparator class='hudson.util.CaseInsensitiveComparator'></comparator>
            </jobNames>
            <jobFilters></jobFilters>
            <columns>
                <hudson.views.StatusColumn></hudson.views.StatusColumn>
                <hudson.views.WeatherColumn></hudson.views.WeatherColumn>
                <hudson.views.JobColumn></hudson.views.JobColumn>
                <hudson.views.LastSuccessColumn></hudson.views.LastSuccessColumn>
                <hudson.views.LastFailureColumn></hudson.views.LastFailureColumn>
                <hudson.views.LastDurationColumn></hudson.views.LastDurationColumn>
                <hudson.views.BuildButtonColumn></hudson.views.BuildButtonColumn>
            </columns>
            <includeRegex>.*</includeRegex>
        </hudson.model.ListView>
    </views>
    <viewsTabBar class='hudson.views.DefaultViewsTabBar'></viewsTabBar>
    <primaryView>All</primaryView>
    <healthMetrics>
        <com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric></com.cloudbees.hudson.plugins.folder.health.WorstChildHealthMetric>
    </healthMetrics>
</com.cloudbees.hudson.plugins.folder.Folder>
'''

}
