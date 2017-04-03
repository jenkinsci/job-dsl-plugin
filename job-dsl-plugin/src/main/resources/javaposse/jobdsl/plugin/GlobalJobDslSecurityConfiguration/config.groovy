package javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration

import lib.FormTagLib

def f = namespace(FormTagLib)

f.optionalBlock(
        field: 'useScriptSecurity',
        title: 'Enable script security for Job DSL scripts',
        checked: descriptor.useScriptSecurity
)
