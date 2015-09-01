job('example-1') {
    publishers {
        archiveXUnit {
            jUnit {
                pattern('my_file.xml')
            }
        }
    }
}

job('example-2') {
    publishers {
        archiveXUnit {
            aUnit {
                pattern('my_file.xml')
            }
            jUnit {
                pattern('my_other_file.xml')
            }
            failedThresholds {
                unstable(10)
                unstableNew(10)
                failure(10)
                failureNew(10)
            }
            skippedThresholds {
                unstable(5)
                unstableNew(5)
                failure(5)
                failureNew(5)
            }
            thresholdMode(ThresholdMode.PERCENT)
            timeMargin(4000)
        }
    }
}
