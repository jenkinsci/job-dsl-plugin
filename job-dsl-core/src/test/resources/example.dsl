job {
    using 'TMPL-library-tests'
    configure {
        name.value = 'project-lib-tests'
        scm.p4view.value = '//depot/project-lib'
    }
}

job {
    using 'TMPL-library-promotion'
    configure {
        scm.p4view.value = '//depot/project-lib'
        chuckNorris.enabled.value = 'true'
    }
}

job {
    using 'TMPL-webapp-tests'
    configure {
        name.value = 'my-webapp'
        scm.p4view.value = '//depot/project-web'
        
    }
}