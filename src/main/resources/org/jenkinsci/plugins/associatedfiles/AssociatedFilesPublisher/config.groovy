import lib.LayoutTagLib

l=namespace(LayoutTagLib)
t=namespace("/lib/hudson")
st=namespace("jelly:stapler")
f=namespace("lib/form")

f.entry(title:"Associated files", field:"associatedFiles") {
  f.textbox()
}

