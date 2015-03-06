package javaposse.jobdsl.dsl

class TestView extends View {
    TestView(JobManagement jobManagement) {
        super(jobManagement)
    }

    @Override
    protected String getTemplate() {
        '<View/>'
    }
}
