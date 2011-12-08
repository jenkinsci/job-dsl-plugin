import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("lib/form")

if (instance?.buildAssociatedFiles != null) { 
  t.summary(icon:"package.png") {
    raw("Associated files:")
    table(class:"fileList") {
      instance.getBuildAssociatedFilesList().each { af ->
        tr() { 
          td() {
            img(src:"${imagesURL}/16x16/text.png", alt:"", height:"16", width:"16")
          }
          td() {
            raw(af)
          }
        }
      }
    }
  }
}