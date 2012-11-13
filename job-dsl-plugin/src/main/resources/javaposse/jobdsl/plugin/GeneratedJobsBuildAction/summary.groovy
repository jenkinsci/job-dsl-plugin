package javaposse.jobdsl.plugin.GeneratedJobsBuildAction;

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("lib/form")

if (my?.modifiedJobs != null) {
    t.summary(icon:"folder.png") {
        raw("Generated Jobs:")
        ul(class:"jobList") {
            my.getModifiedJobs().each { af ->
                li() {
                    a(href:"${rootURL}/job/${af.jobName}/", class:"model-link tl-tr") { raw(af.jobName) }
                }
            }
        }
    }
}