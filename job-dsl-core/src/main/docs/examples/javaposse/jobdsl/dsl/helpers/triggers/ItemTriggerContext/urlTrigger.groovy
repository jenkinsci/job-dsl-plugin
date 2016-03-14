job('example') {
    triggers {
        urlTrigger {
            cron('* 0 * 0 *')
            restrictToLabel('foo')

            // simple configuration
            url('http://www.example.com/foo/') {
                proxy(true)
                status(404)
                timeout(4000)
                check('status')
                check('etag')
                check('lastModified')
            }

            // Content inspection (MD5 hash)
            url('http://www.example.com/bar/') {
                inspection('change')
            }

            // content inspection for JSON content with detailed checking using JSONPath
            url('http://www.example.com/baz/') {
                inspection('json') {
                    path('$.store.book[0].title')
                }
            }

            // content inspection for text content with detailed checking using regular expressions
            url('http://www.example.com/fubar/') {
                inspection('text') {
                    regexp(/_(foo|bar).+/)
                }
            }
        }
    }
}
