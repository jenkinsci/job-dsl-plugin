package javaposse.jobdsl.dsl.helpers.properties

import javaposse.jobdsl.dsl.Context
import javaposse.jobdsl.dsl.Preconditions

class SidebarLinkContext implements Context {
    final List<Node> links = []

    void link(String url, String text, String icon = null) {
        Preconditions.checkNotNullOrEmpty(url, 'url must be specified')
        Preconditions.checkNotNullOrEmpty(text, 'text must be specified')

        links << new NodeBuilder().'hudson.plugins.sidebar__link.LinkAction' {
            delegate.url(url)
            delegate.text(text)
            delegate.icon(icon ?: '')
        }
    }
}
