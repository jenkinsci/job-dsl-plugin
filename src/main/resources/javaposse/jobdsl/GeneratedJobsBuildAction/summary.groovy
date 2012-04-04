package javaposse.jobdsl.GeneratedJobsBuildAction;

import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("lib/form")

if (my?.modifiedJobs != null) { 
  t.summary(icon:"package.png") {
    raw("Generated Jobs:")
    table(class:"jobList") {
      my.getModifiedJobs().each { af ->
        tr() { 
          td() {
            raw(af) // TODO: Make this a link
          }
        }
      }
    }
  }
}