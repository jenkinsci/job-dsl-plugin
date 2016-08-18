package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context

class WallDisplayContext implements Context {
    String name
    String backgroundPicture

    /**
     * Custom text to use for wall display.
     */
    void name(String name) {
        this.name = name
    }

    /**
     * Background picture to use for wall display.
     */
    void backgroundPicture(String picture) {
        backgroundPicture = picture
    }
}
