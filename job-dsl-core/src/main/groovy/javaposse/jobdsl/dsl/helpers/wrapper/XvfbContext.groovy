package javaposse.jobdsl.dsl.helpers.wrapper

import javaposse.jobdsl.dsl.helpers.Context;

class XvfbContext implements Context  {
    String screen = '1024x768x24'
    String installationName = 'xvfb'
    Integer displayNameOffset = 100

    def screen(String screen) {
        this.screen = screen
    }

    def installationName(String installationName) {
        this.installationName = installationName
    }

    def displayNameOffset(Integer displayNameOffset) {
        this.displayNameOffset = displayNameOffset
    }
}
