job('example') {
    triggers {
        gerrit {
            events {
                changeMerged()
                draftPublished()
            }
            project('reg_exp:myProject', ['ant:feature-branch', 'plain:origin/refs/mybranch'])
            project('test-project', '**')
            buildSuccessful(10, null)
        }
    }
}
