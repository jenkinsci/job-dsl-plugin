package javaposse.jobdsl.plugin.ExecuteDslScripts

import javaposse.jobdsl.plugin.ExecuteDslScripts;

def f=namespace(lib.FormTagLib)
if (instance == null) {
    instance = new ExecuteDslScripts()
}

f.radioBlock(name: 'scriptLocation', value: 'true', title: 'Use the provided DSL script', checked: instance.usingScriptText) {
    f.entry(title: 'DSL Script', field: 'scriptText') {
        // TODO CodeMirror support for text/x-groovy. It was unclear how do it from a .groovy stapler script
        f.textarea(style: 'width:100%; height:10em')
    }
}
f.radioBlock(name: 'scriptLocation', value: 'false', title: 'Look on Filesystem', checked: !instance.usingScriptText) {
    f.entry(title: 'DSL Scripts', field: 'targets') {
        f.expandableTextbox()
    }
}

f.entry(title: 'Removed jobs', field: 'deleteJobs') {
    f.radioBlock(value: 'false', title: 'Disable', checked: !instance.deleteJobs)
    f.radioBlock(value: 'true', title: 'Delete', checked: instance.deleteJobs)
}
