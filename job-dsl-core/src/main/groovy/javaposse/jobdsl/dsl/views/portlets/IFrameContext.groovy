package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class IFrameContext implements Context {
    String displayName = 'Iframe Portlet'
    String iframeSource
    String effectiveUrl
    String divStyle = 'width:100%;height:1000px;'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Iframe Portlet'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     *  Sets the iframe source URL.
     */
    void iframeSource(String iframeSource) {
        this.iframeSource = iframeSource
    }

    /**
     * Sets the effective iframe source URL.
     */
    void effectiveUrl(String effectiveUrl) {
        this.effectiveUrl = effectiveUrl
    }

    /**
     * Sets the style for the surrounding div element. Defaults to {@code 'width:100%;height:1000px;'}.
     */
    void divStyle(String divStyle) {
       this.divStyle = divStyle
    }
}
