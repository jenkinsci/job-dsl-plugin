// defining config spec and load rules
job('example-1') {
    scm {
        baseClearCase {
            configSpec('''element * CHECKEDOUT
element * /main/LATEST''')
            loadRules('/vob/some_vob')
        }
    }
}

// defining config spec and load rules with multiple methods calls
job('example-1') {
    scm {
        baseClearCase {
            configSpec('element * CHECKEDOUT')
            configSpec('element * /main/LATEST')
            loadRules('/vob/some_vob')
            loadRules('/vob/another_vob')
        }
    }
}

// defining config spec and load rules using varargs parameters
job('example-1') {
    scm {
        baseClearCase {
            configSpec('element * CHECKEDOUT', 'element * /main/LATEST')
            loadRules('/vob/some_vob', '/vob/another_vob')
        }
    }
}

// reading the config spec from a file in the seed job's workspace
job('example-1') {
    scm {
        baseClearCase {
            configSpec(readFileFromWorkspace('configSpec.txt'))
            loadRules('/vob/some_vob')
        }
    }
}
