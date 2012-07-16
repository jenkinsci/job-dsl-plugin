package javaposse.jobdsl.plugin.GeneratedJobsAction;

import lib.LayoutTagLib

def l=namespace(LayoutTagLib)
def t=namespace("/lib/hudson")

def allJobs = my?.findAllGeneratedJobs()
if (allJobs != null) {
    l.table() {
        t.summary(icon:"folder.png") {
            raw("Generated Jobs:")
            ul(class:"jobList") {
                allJobs.each { af ->
                    li() {
                        a(href:"${rootURL}/job/${af.jobName}/", class:"model-link tl-tr") { raw(af.jobName) }
                    }
                }
            }
        }
    }
}