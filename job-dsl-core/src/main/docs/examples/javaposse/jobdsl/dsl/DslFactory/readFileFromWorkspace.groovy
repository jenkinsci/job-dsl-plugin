// read the file foo/bar.txt from the seed job's workspace
def bar = readFileFromWorkspace('foo/bar.txt')

// read the file acme.xml from a workspace of job project-a
def acme = readFileFromWorkspace('project-a', 'acme.xml')
