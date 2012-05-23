package javaposse.jobdsl.plugin.GeneratedJobsAction;

import lib.LayoutTagLib

def l=namespace(LayoutTagLib)
def t=namespace("/lib/hudson")

if (my?.generatedJobs != null) {
    l.table() {
        t.summary(icon:"folder.png") {
            raw("Generated Jobs:")
            ul(class:"jobList") {
                my.generatedJobs.each { af ->
                    li() {
                        a(href:"${rootURL}/job/${af.jobName}/", class:"model-link tl-tr") { raw(af.jobName) }
                    }
                }
            }
        }
    }
}