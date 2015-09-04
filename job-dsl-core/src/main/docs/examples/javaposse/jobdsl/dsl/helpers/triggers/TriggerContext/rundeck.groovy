job('example') {
    triggers {
        rundeck {
            jobIdentifiers('2027ce89-7924-4ecf-a963-30090ada834f',
                    'my-project-name:main-group/sub-group/my-job')
            executionStatuses('FAILED', 'ABORTED')
        }
    }
}
