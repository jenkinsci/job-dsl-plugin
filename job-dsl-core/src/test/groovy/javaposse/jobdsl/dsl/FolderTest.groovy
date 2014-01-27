package javaposse.jobdsl.dsl

import org.custommonkey.xmlunit.XMLUnit

import com.google.common.collect.Sets;

import spock.lang.Specification

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual

class FolderTest extends Specification {
    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
    }

    def 'construct simple Folder and generate xml from it'() {
        setup:
        JobManagement jm = Mock()
        Set<JobItem> referencedJobs = Sets.newLinkedHashSet()
        Folder folder = new Folder(jm, referencedJobs)

        when:
        def xml = folder.getXml()

        then:
        assertXMLEqual '<?xml version="1.0" encoding="UTF-8"?>' + folderXml, xml
    }

    def 'constuct Job inside the Folder'() {
        setup:
        JobManagement jm = Mock()
        Set<JobItem> referencedJobs = Sets.newLinkedHashSet()
        Folder folder = new Folder(jm, referencedJobs)
        folder.name =  'folder'
        referencedJobs << folder

        when:
        Job job = folder.job {
            name 'job'
        }

        then:
        job.getFullName() == 'folder/job'
        referencedJobs.size() == 2
    }

    def 'constuct Job inside Folder that is in the Folder'() {
        setup:
        JobManagement jm = Mock()
        Set<JobItem> referencedJobs = Sets.newLinkedHashSet()
        Folder folder1 = new Folder(jm, referencedJobs)
        folder1.name =  'folder1'
        referencedJobs << folder1

        when:
        Job innerJob
        folder1.folder {
            name 'folder2'
            innerJob = job {
                name 'job'
            }
        }

        then:
        innerJob.getFullName() == 'folder1/folder2/job'
        referencedJobs.size() == 3
    }

    def 'configure Folder description'() {
        setup:
        final Node project = new XmlParser().parse(new StringReader(folderXml))
        JobManagement jm = Mock()
        Set<JobItem> referencedJobs = Sets.newLinkedHashSet()
        Folder folder = new Folder(jm, referencedJobs)

        when: 'Simple update'
        folder.configure { Node node ->
            node / description('Test Description')
        }
        folder.executeWithXmlActions(project)

        then:
        project.description[0].text() == 'Test Description'
    }

    final folderXml = '''
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
