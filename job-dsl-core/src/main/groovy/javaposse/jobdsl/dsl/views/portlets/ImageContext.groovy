package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class ImageContext implements Context {
    String displayName = 'Image Portlet'
    String url

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Image Portlet'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     * Sets the effective image source URL.
     */
    void url(String url) {
        this.url = url
    }

}

