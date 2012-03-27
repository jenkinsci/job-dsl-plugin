package javaposse.jobdsl.GeneratedJobsAction;

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("lib/form")

if (my?.jobNames != null) { 
  t.summary(icon:"package.png") {
    raw("Generated Jobs:")
    table(class:"jobList") {
      my.getJobNames().each { af ->
        tr() { 
          td() {
            raw(af) // TODO: Make this a link
          }
        }
      }
    }
  }
}