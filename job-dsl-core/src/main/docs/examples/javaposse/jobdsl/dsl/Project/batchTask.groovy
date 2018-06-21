job('example') {
    batchTask('upload', 'curl --upload-file build/dist.zip http://www.example.com/upload')
    batchTask('release', readFileFromWorkspace('scripts/release.sh'))
}
