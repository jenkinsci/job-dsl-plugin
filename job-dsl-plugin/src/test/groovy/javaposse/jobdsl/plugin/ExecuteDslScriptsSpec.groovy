package javaposse.jobdsl.plugin

import org.jvnet.hudson.test.TemporaryDirectoryAllocator
import spock.lang.Specification

class ExecuteDslScriptsSpec extends Specification {

    ExecuteDslScripts executeDslScripts = new ExecuteDslScripts()

    def 'getIncludedFiles'() {
        given:
        def temporaryDirectoryAllocator = new TemporaryDirectoryAllocator();
        File dir = temporaryDirectoryAllocator.allocate()
        new File(dir, 'test1.groovy').createNewFile()
        new File(dir, 'test2.groovy').createNewFile()
        new File(dir, 'foo.groovy').createNewFile()
        File subDir = new File(dir, 'dir1')
        subDir.mkdir()
        new File(subDir, 'test3.groovy').createNewFile()

        expect:
        executeDslScripts.getIncludedFiles(pattern, dir).sort() == includedFiles.sort()

        cleanup:
        temporaryDirectoryAllocator.dispose()

        where:
        pattern          || includedFiles
        'test*'          || ['test1.groovy', 'test2.groovy']
        '**/*test*'      || ['test1.groovy', 'test2.groovy', 'dir1/test3.groovy']
        'xxx*'           || []
        'test1.groovy'   || ['test1.groovy']
        '**/*foo.groovy' || ['foo.groovy']
    }
}
