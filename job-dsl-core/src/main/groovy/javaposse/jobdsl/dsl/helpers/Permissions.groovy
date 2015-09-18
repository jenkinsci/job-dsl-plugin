package javaposse.jobdsl.dsl.helpers

@Deprecated
enum Permissions {
    ItemDelete('hudson.model.Item.Delete'),
    ItemConfigure('hudson.model.Item.Configure'),
    ItemRead('hudson.model.Item.Read'),
    ItemDiscover('hudson.model.Item.Discover'),
    ItemBuild('hudson.model.Item.Build'),
    ItemWorkspace('hudson.model.Item.Workspace'),
    ItemCancel('hudson.model.Item.Cancel'),
    ItemRelease('hudson.model.Item.Release'),
    ItemExtendedRead('hudson.model.Item.ExtendedRead'),
    RunDelete('hudson.model.Run.Delete'),
    RunUpdate('hudson.model.Run.Update'),
    ScmTag('hudson.scm.SCM.Tag')

    final String longForm

    Permissions(String longForm) {
        this.longForm = longForm
    }
}
