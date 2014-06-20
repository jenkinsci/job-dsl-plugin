package javaposse.jobdsl.dsl.helpers.scm

import spock.lang.Specification

class PerforcePasswordEncryptorSpec extends Specification {
    def 'test isEncrypted'(String password, boolean expectedResult) {
        when:
        boolean actualResult = PerforcePasswordEncryptor.isEncrypted(password)

        then:
        actualResult == expectedResult

        where:
        password         | expectedResult
        null             | false
        ''               | false
        'foo'            | false
        '0f0kqlwalalala' | true
        '0f0kqlwa'       | true
    }

    def 'test encrypt'(String password, String expectedResult) {
        when:
        String actualResult = PerforcePasswordEncryptor.encrypt(password)

        then:
        actualResult == expectedResult

        where:
        password | expectedResult
        null     | ''
        ''       | ''
        '   '    | ''
        'foo'    | '0f0kqlwar3Axs06SekM='
        'bar'    | '0f0kqlwa4cYGMOmO7GY='
    }
}
