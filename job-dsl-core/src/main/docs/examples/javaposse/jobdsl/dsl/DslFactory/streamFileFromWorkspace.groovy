// read the file images/logo.png from the seed job's workspace
// and save the as user content
def image = streamFileFromWorkspace('images/logo.png')
userContent('company-logo.png', image)
