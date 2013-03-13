package javaposse.jobdsl.plugin.ExecuteDslScripts

import javaposse.jobdsl.plugin.RemovedJobAction;
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

f.entry(title: 'Action for existings jobs:', field: 'ignoreExisting') {
    f.checkbox(name: 'ignoreExisting', title: 'Ignore changes', checked: instance.ignoreExisting,
		description: 'What to do with previously generated jobs when generated config is not the same?')
}

f.entry(title: 'Action for removed jobs:', field:'removedJobAction',
	description: 'What to do when a previously generated job is not referenced anymore?') {
	select(name:'removedJobAction') {
		f.option(value:'IGNORE', selected:instance.removedJobAction==RemovedJobAction.IGNORE, 'Ignore')
		f.option(value:'DISABLE', selected:instance.removedJobAction==RemovedJobAction.DISABLE, 'Disable')
		f.option(value:'DELETE', selected:instance.removedJobAction==RemovedJobAction.DELETE, 'Delete')
	}
}
