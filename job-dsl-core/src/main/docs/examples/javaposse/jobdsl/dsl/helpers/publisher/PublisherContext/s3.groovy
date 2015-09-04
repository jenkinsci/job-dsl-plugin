job('example') {
    publishers {
        s3('myProfile') {
            entry('foo', 'bar', 'eu-west-1') {
                storageClass('REDUCED_REDUNDANCY')
                noUploadOnFailure()
                uploadFromSlave()
            }
        }
    }
}
