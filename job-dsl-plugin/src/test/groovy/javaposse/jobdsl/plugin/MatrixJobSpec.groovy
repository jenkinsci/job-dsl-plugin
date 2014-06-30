package javaposse.jobdsl.plugin

import hudson.model.FreeStyleProject
import hudson.model.JDK
import hudson.slaves.DumbSlave
import javaposse.jobdsl.plugin.ExecuteDslScripts
import spock.lang.Specification
import spock.lang.Unroll
import org.junit.Rule
import org.jvnet.hudson.test.JenkinsRule

class MatrixJobSpec extends Specification {

    @Rule
    JenkinsRule rule = new JenkinsRule()

    FreeStyleProject configure(script) {
        def job = rule.createFreeStyleProject("seed")
        def builder = new ExecuteDslScripts(script)

        job.getBuildersList().add(builder)
        job
    }

    def 'NoAxis'() {
        given:
        def job = configure('job(type:MatrixJob){name "generated"}')

        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 1
    }

    def 'TextAxis'() {
        given:
        def job = configure($/
job(type:MatrixJob){
  name "generated" 
  axis{ 
    text("test", ["a", "b","c"])
  }
}
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 3
    }

    def 'LabelAxis'() {
        given:
        def job = configure($/ 
job(type:MatrixJob){
  name "generated" 
  axis{ 
    label("test", ["label1", "label2","label3"])
  }
}
/$)
        rule.createSlave("Node1", "label1", null)
        rule.createSlave("Node2", "label2", null)
        rule.createSlave("Node3", "label3", null)

        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 3
    }

    //@Unroll
    def 'LabelExpressionAxis'() {
        given:
        def job = configure($/ 
job(type:MatrixJob){
  name "generated"
  axis{
    labelExpression("test", ["label"])
  }
}
/$)
        rule.createSlave("Node1", "label", null)
        rule.createSlave("Node2", "label", null)
        rule.createSlave("Node3", "label", null)

        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 1
    }

    def 'JDKAxis'() {
        given:
        def job = configure($/ 
job(type:MatrixJob){
  name "generated"
  axis{
    jdk("test", ["jdk1"])
  }
}
/$)
        rule.getInstance().getJDKs().add(new JDK("default",System.getProperty("java.home")))
        rule.getInstance().getJDKs().add(new JDK("jdk1",System.getProperty("java.home")))
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 1
    }

    def 'TwoAxes'() {
        given:
        def job = configure($/ 
job(type:MatrixJob){
  name "generated"
  axis{
    jdk("test", ["jdk1", "default"])
    text("test2", ["text1", "text2"])
  }
}
/$)
        rule.getInstance().getJDKs().add(new JDK("default",System.getProperty("java.home")))
        rule.getInstance().getJDKs().add(new JDK("jdk1",System.getProperty("java.home")))
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 4
    }

    def 'ConfigAxes'() {
        given:
        def job = configure($/ 
job(type:MatrixJob){
  name "generated"
  axis{
    text("test2", ["text1", "text2"])

    configure { project ->
      def ax = project/"axes"
      ax << {
          "hudson.matrix.TextAxis" {
              delegate.createNode("name", "axis1")
              values {
                  string "z"
                  string "y"
                  string "x"
              }
          }
      }
    }
  }
}
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 6
    }

    def 'Touchstone'() {
        given:
        def job = configure( $/
job(type:MatrixJob){
  name "generated"
  axis{
    text("axis1", ["textz", "texty"])
    text("axis2", ["text1", "text2"])
  }
  steps{
    shell('return 255')
  }
  touchStoneFilter("axis1=='textz' && axis2=='text2'", false)
  sequential(false)
} 
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("FAILURE")
        gen_runs.every(){it.logFile.text.contains("FAILURE")}
        gen_runs.every(){it.getBuildVariables().equals([axis1: 'textz', axis2: 'text2'])}
        gen_runs.size() == 1
    }

    def 'CombinationFilter'() {
        given:
        def job = configure( $/
job(type:MatrixJob){
  name "generated"
  axis{
    text("axis1", ["textz", "texty"])
    text("axis2", ["text1", "text2"])
  }
  steps{
    shell('return 255')
  }
  combinationFilter("axis1=='textz' || axis2=='text2'")
  sequential(false)
} 
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("FAILURE")
        gen_runs.every(){it.logFile.text.contains("FAILURE")}
        gen_runs.every(){it.getBuildVariables() in [
          [axis1: 'textz', axis2: 'text1'],
          [axis1: 'textz', axis2: 'text2'],
          [axis1: 'texty', axis2: 'text2']]}
        gen_runs.size() == 3
    }

    //this should fail...
    def 'RedefineFreestyle'() {
        given:
        def orig = rule.createFreeStyleProject('generated')
        orig.scheduleBuild2(0).get()

        def job = configure($/
job(type:MatrixJob){
  name "generated" 
  axis{ 
    text("test", ["a", "b","c"])
  }
}
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        //def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        //gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        //gen_runs.size() == 3
    }

    def 'RedefinMatrixJob'() {
        given:
        rule.createMatrixProject('generated')
        def job = configure($/
job(type:MatrixJob){
  name "generated" 
  axis{ 
    text("test", ["a", "b","c"])
  }
}
/$)
        when:
        def job_build = job.scheduleBuild2(0).get()

        def gen = rule.getInstance().getItem("generated")
        def gen_build = gen.scheduleBuild2(0).get()
        def gen_runs = gen_build.getRuns()

        then:
        job_build.logFile.text.contains("SUCCESS")
        gen_build.logFile.text.contains("SUCCESS")
        gen_runs.every(){it.logFile.text.contains("SUCCESS")}
        gen_runs.size() == 3
    }
}
