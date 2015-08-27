(function($) {

    /**
     * Loads and caches DSL data.
     */
    var DslLoader = function() {
        this.dslsByUrl = {};
    };
    _.extend(DslLoader.prototype, {

        fetch: function(url) {
            var dsl = this.dslsByUrl[url];
            if (!dsl) {
                return $.get(url).then(function(data) {
                    var dsl = new Dsl(data);
                    this.dslsByUrl[url] = dsl;
                    return dsl;
                }.bind(this));
            }
            return $.Deferred().resolveWith(null, [dsl]);
        }
    });

    /**
     * Provides access to DSL data.
     */
    var Dsl = function(data) {
        this.data = data;
        _.forEach(data.contexts, this._processContext.bind(this));
    };
    _.extend(Dsl.prototype, {

        _processContext: function(context) {
            var tokens = context.type.split('.');
            context.simpleClassName = tokens[tokens.length - 1];

            context.methods.forEach(function(method) {
                if (method.signatures.every(function(sig) { return sig.deprecated; })) {
                    method.deprecated = true;
                }

                var signatureWithContext = _.find(method.signatures, function(signature) { return signature.contextClass; });
                if (signatureWithContext) {
                    method.contextClass = signatureWithContext.contextClass;
                }

                if (method.plugin) {
                    method.plugin = window.updateCenter.data.plugins[method.plugin];
                }
            });
        },

        getContext: function(contextClass) {
            return this.data.contexts[contextClass];
        },

        getRootContextClass: function() {
            return this.data.root.contextClass;
        },

        getPluginList: function() {
            var plugins = [];

            _.forEach(this.data.contexts, function(context) {
                context.methods.forEach(function(method) {
                    if (method.plugin) {
                        plugins.push(method.plugin);
                    }
                });
            });

            plugins = _.uniq(plugins);
            return _.sortBy(plugins, 'name')
        },

        findUsages: function(contextClass) {
            var usages = [];
            _.forEach(this.data.contexts, function(context, clazz) {
                context.methods.forEach(function(method) {
                    if (method.contextClass === contextClass) {
                        usages.push({
                            method: method,
                            context: context,
                            simpleClassName: context.simpleClassName
                        });
                    }
                });
            });
            return usages;
        },

        findPluginUsages: function(plugin) {
            var usages = [];
            _.forEach(this.data.contexts, function(context) {
                context.methods.forEach(function(method) {
                    if (method.plugin === plugin) {
                        usages.push({method: method, context: context});
                    }
                });
            });
            return usages;
        },

        findMethodNode: function(contextClass, tokens) {
            var methodNode = null;
            var node = this.data.contexts[contextClass];

            tokens.forEach(function(token) {
                methodNode = _.find(node.methods, function(method) { return method.name === token; });
                node = this.data.contexts[methodNode.contextClass];
            }, this);

            return methodNode;
        },

        findAncestors: function(contextClass, tokens) {
            var ancestors = [];

            tokens.forEach(function(token, index) {
                if (index < tokens.length - 1) {
                    var id = tokens.slice(0, index + 1).join('-');
                    ancestors.push({
                        id: id,
                        text: token
                    });
                }
            }, this);

            return ancestors;
        },

        getContextSignatures: function(contextClass, path) {
            var signatures = [];

            this.data.contexts[contextClass].methods.forEach(function(method) {
                var methodPath = (path ? path + '-' : '') + method.name;
                Array.prototype.push.apply(signatures, this.getSignatures(method, methodPath));
            }, this);

            return signatures;
        },

        getSignatures: function(method, path) {
            var href = '#path/' + (path ? path + '-' : '') + method.name;
            return method.signatures.map(function(signature) {

                if (signature.contextClass) {
                    signature.context = this.data.contexts[signature.contextClass];
                } // TODO

                var params = signature.parameters;
                if (signature.context) {
                    params = params.slice(0, params.length - 1);
                }
                var paramTokens = params.map(function(param) {
                    var token = param.type + ' ' + param.name;
                    if (param.defaultValue) {
                        token += ' = ' + param.defaultValue;
                    }
                    return token;
                });
                var text = paramTokens.join(', ');
                if (paramTokens.length || !signature.context) {
                    text = '(' + text + ')';
                }

                return {
                    name: method.name,
                    href: href,
                    path: path,
                    availableSince: signature.availableSince,
                    deprecated: signature.deprecated,
                    text: text,
                    html: signature.html,
                    context: signature.context,
                    comment: signature.firstSentenceCommentText
                };
            }, this)
        },

        getPathInfo: function(path) {
            var methodNode;
            var ancestors = [];
            var usages = [];
            if (path) {
                var tokens = path.split('-');

                var contextClass;
                var pathTokens;
                var methodIndex = tokens[0].lastIndexOf('.');
                if (methodIndex === -1) { // absolute
                    contextClass = this.data.root.contextClass;
                    pathTokens = tokens;
                } else { // relative
                    var methodName = tokens[0].substr(methodIndex + 1);

                    contextClass = tokens[0].substr(0, methodIndex);
                    pathTokens = [methodName].concat(tokens.slice(1));
                    usages = this.findUsages(contextClass);
                }

                methodNode = this.findMethodNode(contextClass, pathTokens);
                ancestors = this.findAncestors(contextClass, pathTokens);

                if (ancestors.length) {
                    ancestors[0].id = contextClass + '.' + ancestors[0].id;
                }
            } else {
                methodNode = this.data.root;
            }

            return {
                methodNode: methodNode,
                ancestors: ancestors,
                usages: usages
            };
        },

        getAllContexts: function() {
            return this.data.contexts;
        }
    });

    var App = function() {
        this.dslLoader = new DslLoader();

        this.initLayout();
        this.loadSelectedDsl();

        $('.version-select').change(this.loadSelectedDsl.bind(this));

        window.addEventListener('hashchange', this.onHashChange.bind(this), false);

        $('.search-input').keyup(this.onSearch.bind(this));
        $('.clear-search').click(function(event) {
            event.preventDefault();
            $('.search-input').val('');
            this.onSearch();
        }.bind(this));
    };

    _.extend(App.prototype, {

        onHashChange: function(e) {
            e.preventDefault();
            e.stopPropagation();

            this.updateDetailFromHash();
            this.updateTreeFromHash();
        },

        updateDetailFromHash: function() {
            var hashId = window.location.hash;
            if (hashId) {
                hashId = hashId.substring(1);
                var index = hashId.indexOf('/');
                if (index !== -1) {
                    var type = hashId.substr(0, index);
                    var value = hashId.substr(index + 1);

                    if (type === 'plugin') {
                        var plugin = _.find(this.plugins, function(plugin) { return plugin.name === value; });
                        this.showPluginDetail(plugin);
                    } else {
                        this.showPathDetail(value);
                    }
                }
            } else {
                this.showPathDetail();
            }
            this.layout.resizeAll();
        },

        loadSelectedDsl: function() {
            var url = $('.version-select').val();
            this.dslLoader.fetch(url).then(this.onDslFetchComplete.bind(this));
        },

        onDslFetchComplete: function(dsl) {
            this.dsl = dsl;
            this.plugins = this.dsl.getPluginList();
            this.initTree();

            var allItems = [];
            _.forEach(this.dsl.getAllContexts(), function(context, clazz) {
                context.methods.forEach(function(method) {
                    allItems.push({
                        name: method.name,
                        clazz: clazz,
                        simpleClassName: context.simpleClassName
                    });
                });
            });

            allItems = allItems.concat(this.plugins.map(function(plugin) {
                return {
                    id: plugin.name,
                    name: plugin.title
                };
            }));
            allItems = _.sortBy(allItems, function(item) { return item.name.toLowerCase(); });
            this.allItems = allItems;

            this.updateDetailFromHash();
        },

        onSearch: function() {
            var val = $('.search-input').val();
            $('.clear-search').toggleClass('hide', !val);
            var $treeBody = $('.tree-body');
            var $searchResults = $('.search-results');
            if (val) {
                if ($treeBody.is(':visible')) {
                    $treeBody.hide();
                    $searchResults.show();
                }

                var matches = this.allItems.filter(function(item) {
                    return item.name.toLowerCase().indexOf(val) !== -1; // TODO
                }, this);
                var html = Handlebars.templates['searchResults']({results: matches});
                $searchResults.html(html);
                // update result list
            } else {
                $treeBody.show();
                $searchResults.hide();
            }
        },

        initLayout: function() {
            this.layout = $('.layout-container').layout({
                north__paneSelector: '.title',
                north__spacing_open: 0,
                west__paneSelector: '.tree',
                west__contentSelector: '.tree-wrapper',
                west__size: 360,
                west__minSize: 360,
                west__spacing_open: 3,
                west__resizerCursor: 'ew-resize',
                center__paneSelector: '.detail-wrapper',
                north__size: 50,
                resizable: true,
                closable: false,
                enableCursorHotkey: false
            });
        },

        initTree: function() {
            var $treeBody = $('.tree-body');

            var updateNodes = function($el) {
                $el.parent().find('.jstree-open > i.jstree-icon')
                    .removeClass('glyphicon-triangle-right').addClass('glyphicon glyphicon-triangle-bottom');
                $el.parent().find('.jstree-closed > i.jstree-icon')
                    .removeClass('glyphicon-triangle-bottom').addClass('glyphicon glyphicon-triangle-right');
            };
            $treeBody.on('open_node.jstree', function(e, data){
                updateNodes($('#'+data.node.id));
            });

            $treeBody.on('close_node.jstree', function(e, data){
                updateNodes($('#'+data.node.id));
            });

            $treeBody
                .jstree('destroy')
                .on('changed.jstree', this.onTreeChanged.bind(this))
                .on('ready.jstree', function() {
                    this.updateTreeFromHash();
                    var selectedNodes = this.jstree.get_selected(true);
                    if (selectedNodes.length) {
                        $('#' + selectedNodes[0].id)[0].scrollIntoView();
                    }
                    updateNodes($('.tree-body'));
                }.bind(this))
                .jstree({
                    'plugins': ['wholerow'],
                    'core': {
                        'data': function(node, cb) {
                            var contextClass = node.id === '#' ? this.dsl.getRootContextClass() : node.original.methodNode.contextClass;
                            var methods = this.dsl.getContext(contextClass).methods;
                            var treeNodes = methods.map(function(method) {
                                return this.buildJstreeNode(method, node);
                            }, this);

                            cb(treeNodes);
                        }.bind(this),
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

        onTreeChanged: function(e, data) {
            e.preventDefault();
            window.location.hash = 'path/' + data.node.id;
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
                    }
                }, this);
            }
        },

        showPluginDetail: function(plugin) {
            var usages = this.dsl.findPluginUsages(plugin);
            var html = Handlebars.templates['pluginDetail']({plugin: plugin, usages: usages});
            $('.detail-wrapper').html(html);
        },

        showPathDetail: function(path) {
            var pathInfo = this.dsl.getPathInfo(path);
            var methodNode = pathInfo.methodNode;
            var ancestors = pathInfo.ancestors;
            var usages = pathInfo.usages;

            var data = {
                methodNode: methodNode,
                name: methodNode.name,
                ancestors: ancestors
            };

            if (methodNode.signatures) {
                data.signatures = this.dsl.getSignatures(methodNode, path)
            }

            data.usages = _.sortBy(usages, function(usage) { return (usage.method.name + usage.simpleClassName).toLowerCase(); });

            var html,
                $detailWrapper = $('.detail-wrapper');
            if (path) {
                html = Handlebars.templates['detail'](data);
                $detailWrapper.html(html);
            } else {
                html = Handlebars.templates['root'](data);
                $detailWrapper.html(html);

                var signatures = this.dsl.getContextSignatures(methodNode.contextClass, path);

                var contextHtml = Handlebars.templates['context']({
                    signatures: signatures
                });
                $detailWrapper.find('.context-methods-section').html(contextHtml);
            }

            this.highlightCode($('.highlight'));

            $detailWrapper.find('.expand-closure').click(this.onExpandClick.bind(this));
        },

        onExpandClick: function(e) {
            e.preventDefault();
            var $el = $(e.currentTarget);
            var path = $el.data('path');

            $el.hide();

            var pathInfo = this.dsl.getPathInfo(path);
            var signatures = this.dsl.getContextSignatures(pathInfo.methodNode.contextClass, path);

            var contextHtml = Handlebars.templates['context']({
                signatures: signatures
            });
            var $contextHtml = $(contextHtml);
            $contextHtml.insertAfter($el);

            this.highlightCode($contextHtml.find('.highlight'));

            $contextHtml.find('.expand-closure').click(this.onExpandClick.bind(this));
        },

        highlightCode: function($elements) {
            $elements.each(function(i, block) {
                hljs.highlightBlock(block);
                $(block).removeClass('ruby'); // TODO hljs bug?
            });
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

    $(function() {
        new App();
    });
}(jQuery));