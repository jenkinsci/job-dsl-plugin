package javaposse.jobdsl.plugin.GlobalJobDslSecurityConfiguration

import lib.FormTagLib

def f = namespace(FormTagLib)

f.section(title:_('Job DSL Security')) {
    f.optionalBlock(
            field: 'useScriptSecurity',
            title: 'Enable script security for Job DSL scripts',
            checked: descriptor.useScriptSecurity
    )
    f.optionalBlock(
            field: 'restrictJobPathBySeedJob',
            title: 'Restrict seed jobs to manage items only in the seed\'s folder and its subfolders',
            checked: descriptor.restrictJobPathBySeedJob
    )
}
