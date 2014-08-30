package javaposse.jobdsl.dsl

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileJobManagementSpec extends Specification {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    def 'createOrUpdateConfigFile is not supported'() {
        when:
        new FileJobManagement(temporaryFolder.newFolder()).createOrUpdateConfigFile(Mock(ConfigFile), false)

        then:
        thrown(UnsupportedOperationException)
    }
}
