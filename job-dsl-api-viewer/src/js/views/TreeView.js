App.TreeView = Marionette.ItemView.extend({

    template: 'tree',

    initialize: function(options) {
        this.dsl = options.dsl;
        this.settings = options.settings;

        this.listenTo(this.settings, 'change', function() {
            this.jstree.refresh();
            this._updateNodeIcons(this.$('.tree-body'));
        });
    },

    _updateNodeIcons: function($el) {
        $el.parent().find('.jstree-open > i.jstree-icon')
            .removeClass('glyphicon-triangle-right').addClass('glyphicon glyphicon-triangle-bottom');
        $el.parent().find('.jstree-closed > i.jstree-icon')
            .removeClass('glyphicon-triangle-bottom').addClass('glyphicon glyphicon-triangle-right');
    },

    onRender: function() {
        var $treeBody = this.$('.tree-body');
        $treeBody.on('open_node.jstree', this.onOpenNode.bind(this));
        $treeBody.on('close_node.jstree', this.onCloseNode.bind(this));

        $treeBody
            .jstree('destroy')
            .on('changed.jstree', this.onTreeChanged.bind(this))
            .on('ready.jstree', this.onTreeReady.bind(this))
            .jstree({
                'plugins': ['wholerow'],
                'core': {
                    'animation': false,
                    'data': this.loadTreeData.bind(this),
                    'themes': {
                        'name': 'proton',
                        'responsive': true
                    },
                    'multiple': false,
                    'worker': false
                }
            });
        this.jstree = $treeBody.jstree();
    },

    loadTreeData: function(node, cb) {
        var contextClass = node.id === '#' ? this.dsl.getRootContextClass() : node.original.methodNode.contextClass;
        var methods = _.chain(this.dsl.getContext(contextClass).methods)
            .filter(function (method) {
                return !method.plugin || !this.settings.isPluginExcluded(method.plugin.name);
            }.bind(this))
            .sortBy(function (method) {
                return method.name.toLowerCase();
            })
            .value();
        var treeNodes = methods.map(function(method) {
            return this.buildJstreeNode(method, node);
        }, this);

        cb(treeNodes);
    },

    onOpenNode: function(e, data){
        var el = document.getElementById(data.node.id);
        this._updateNodeIcons($(el));
    },

    onCloseNode: function(e, data) {
        var el = document.getElementById(data.node.id);
        this._updateNodeIcons($(el));
    },

    onTreeReady: function() {
        this.updateTreeFromHash();
        this._updateNodeIcons(this.$('.tree-body'));
    },

    onTreeChanged: function(e, data) {
        e.preventDefault();
        if (data.node) {
            var path = data.node.id;
            window.location.hash = 'path/' + path;
        }
    },

    updateTreeFromHash: function() {
        var hashId = window.location.hash;
        this.jstree.deselect_all(true);

        if (hashId && hashId.indexOf('#path/') === 0) {
            $('.tree-body').show();
            $('.search-results').hide();

            var path = hashId.substring(6);
            var tokens = path.split('-');
            tokens.forEach(function(token, index) {
                var id = tokens.slice(0, index + 1).join('-');
                var node = this.jstree.get_node(id);
                if (index < tokens.length - 1) {
                    this.jstree.open_node(node);
                } else {
                    this.jstree.select_node(node.id);
                    var $el = $('#' + node.id);
                    if ($el.length) { // make sure selected node is visible
                        var $wrapper = $('.tree-wrapper');
                        if ($el.offset().top < $wrapper.offset().top ||
                            $el.offset().top + $el.height() > $wrapper.offset().top + $wrapper.height()) {
                            $el[0].scrollIntoView();
                        }
                    }
                }
            }, this);
        }
    },

    buildJstreeNode: function(node, parent) {
        var id = parent.id === '#' ? node.name : parent.id + '-' + node.name;
        var treeNode = {
            id: id,
            text: node.name,
            icon: false,
            methodNode: node,
            children: !!(node.contextClass)
        };

        if (node.deprecated) {
            treeNode.a_attr = {'class': 'deprecated'};
        }
        return treeNode;
    }
});
