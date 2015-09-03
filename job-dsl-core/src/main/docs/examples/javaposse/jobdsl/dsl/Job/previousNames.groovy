// keep the build history when re-numbering jobs
job('04-project-a-deploy') {
    previousNames(/\d+-project-a-deploy/)
}
