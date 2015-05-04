package javaposse.jobdsl.dsl.helpers.properties

import com.google.common.base.Preconditions
import com.google.common.base.Strings
import javaposse.jobdsl.dsl.Context

class SidebarLinkContext implements Context {
    final List<Node> links = []

    void link(String url, String text, String icon = null) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(url), 'url must be specified')
        Preconditions.checkArgument(!Strings.isNullOrEmpty(text), 'url must be specified')

        links << new NodeBuilder().'hudson.plugins.sidebar__link.LinkAction' {
            delegate.url(url)
            delegate.text(text)
            delegate.icon(icon ?: '')
        }
    }
}
