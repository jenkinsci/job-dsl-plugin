job('example') {
    publishers {
        s3('myProfile') {
            entry('foo', 'bar', 'EU_WEST_1') {
                storageClass('REDUCED_REDUNDANCY')
                noUploadOnFailure()
                uploadFromSlave()
            }
        }
    }
}
