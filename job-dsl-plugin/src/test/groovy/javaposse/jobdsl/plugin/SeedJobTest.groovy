package javaposse.jobdsl.plugin

import org.junit.Test

public class SeedJobTest { //  extends HudsonTestCase {
//    @Rule
//    public JenkinsRule j = new JenkinsRule();

    private static final String templateProjectName = "TMPL";

    @Test
    public void createTemplateTest() throws Exception {
//        java.lang.NoClassDefFoundError: hudson/tasks/Ant$AntInstallation
//        FreeStyleProject project = j.createFreeStyleProject()
//        project.getBuildersList().add(new Shell("echo hello"))
//        project.setDisplayName(templateProjectName)
    }

//    @Test
//    public void createSeedJob() throws Exception {
//        String templateProjectName = createTemplateTest();
//
//        String dsl = '''
//job {
//    using 'TMPL'
//    name 'unit-test'
//}
//'''
//        FreeStyleProject project = j.createFreeStyleProject();
//        project.getBuildersList().add(new ExecuteDslScripts(dsl));
//
//        FreeStyleBuild build = project.scheduleBuild2(0).get();
//        System.out.println(build.getDisplayName()+" completed");
//
//        String s = FileUtils.readFileToString(build.getLogFile());
//        assertThat(s, contains("+ echo hello"));
//
//        // TODO Check for new "unit-test" job
//        Project createdProj = (Project) j.jenkins.getItem('unit-test')
//    }
}
