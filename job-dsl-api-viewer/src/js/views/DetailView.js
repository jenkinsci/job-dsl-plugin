App.DetailView = Marionette.ItemView.extend({

    template: 'detail',

    initialize: function(options) {
        this.dsl = options.dsl;
        this.path = options.path;
        this.settings = options.settings;
        this.listenTo(this.settings, 'change', this.render);
    },

    serializeData: function() {
        var pathInfo = this.dsl.getPathInfo(this.path);
        var methodNode = pathInfo.methodNode;
        var ancestors = pathInfo.ancestors;
        var usages = pathInfo.usages;

        var data = {
            methodNode: methodNode,
            name: methodNode.name,
            ancestors: ancestors
        };

        if (methodNode.signatures) {
            data.signatures = this.dsl.getSignatures(methodNode, this.path);
        }

        data.usages = _.sortBy(usages, function(usage) { return (usage.method.name + usage.simpleClassName).toLowerCase(); });

        return data;
    }

});
