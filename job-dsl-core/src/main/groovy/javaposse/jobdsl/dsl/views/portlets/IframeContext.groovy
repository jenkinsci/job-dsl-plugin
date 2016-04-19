package javaposse.jobdsl.dsl.views.portlets

import javaposse.jobdsl.dsl.Context

class IframeContext  implements Context {

    String displayName = 'Iframe Portlet'
    String iframeSource = ''
    String effectiveSource = ''
    String effectiveUrl = ''
    String divStyle = 'width:100%;height:1000px;'

    /**
     * Sets the display name for the portlet. Defaults to {@code 'Iframe Portlet'}.
     */
    void displayName(String displayName) {
        this.displayName = displayName
    }

    /**
     *  Sets the iframe source for portlet. Defaults is empty.
     */
    void iframeSource(String iframeSource) {
        this.iframeSource = iframeSource
    }

    /**
     * Sets the effective source for portlet. Defaults is empty.
     */
    void effectiveSource(String effectiveSource) {
        this.effectiveSource = effectiveSource
    }

    /**
     * Sets the effective url for portlet. Defaults is empty.
     */
    void effectiveUrl(String effectiveUrl) {
        this.effectiveUrl = effectiveUrl
    }

    /**
     * Sets the div style for portlet. Defaults to {@code 'width:100%;height:1000px;'}.
     */
    void divStyle(String divStyle) {
       this.divStyle = divStyle
    }
}
