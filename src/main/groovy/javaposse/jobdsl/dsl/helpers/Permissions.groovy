package javaposse.jobdsl.dsl.helpers

enum Permissions {
    ItemConfigure('hudson.model.Item.Configure'),
    ItemWorkspace('hudson.model.Item.Workspace'),
    ItemDelete('hudson.model.Item.Delete'),
    ItemBuild('hudson.model.Item.Build'),
    ItemRead('hudson.model.Item.Read'),
    ItemRelease('hudson.model.Item.Release'),
    ItemExtendedRead('hudson.model.Item.ExtendedRead'),
    RunDelete('hudson.model.Run.Delete'),
    RunUpdate('hudson.model.Run.Update')

    final String longForm
    Permissions(String longForm) {
        this.longForm = longForm
    }
}