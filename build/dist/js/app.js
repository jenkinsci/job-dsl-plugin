// Use Handlebars to render marionette views.
Marionette.Renderer.render = function(template, data) {
    if (typeof template === 'function'){
        return template(data);
    }
    var fcn = Handlebars.templates[template];
    return fcn ? fcn(data) : '';
};

(function($) {

    window.App = new Backbone.Marionette.Application({

        onStart: function(options) {

            this.addRegions({
                pluginsRegion:  '.plugins-body',
                treeRegion:     '.tree-wrapper',
                detailRegion:   '.detail-wrapper'
            });

            this.dslLoader = new App.DslLoader();
            this.settings = new App.Settings();

            this.router = new App.Router();
            this.router.on('route:home',    this.showHome, this);
            this.router.on('route:path',    this.showPath, this);
            this.router.on('route:method',  this.showPath, this);
            this.router.on('route:plugin',  this.showPlugin, this);

            this.initLayout();
            $('.loading-outer').addClass('loading');
            this.loadSelectedDsl().then(function() {
                Backbone.history.start({pushState: false});
                if (!this.dsl.isEmbedded()) {
                    $('.version-select').show()
                }
            }.bind(this));

            $('.version-select').change(function() {
                this.loadSelectedDsl().then(function() {
                    Backbone.history.loadUrl(Backbone.history.getHash());
                });
            }.bind(this));
            $('.toggle-plugins').click(function(e) {
                if ($('.plugins-wrapper').is(':visible')) {
                    this.layout.hide('east');
                } else {
                    this.layout.show('east');
                }
            }.bind(this));

            $('.search-input').keyup(this.onSearch.bind(this));
            $('.clear-search').click(function(event) {
                event.preventDefault();
                $('.search-input').val('');
                this.onSearch();
            }.bind(this));
        },

        showPlugin: function(name) {
            var plugin = _.findWhere(this.plugins, {name: name});
            var usages = this.dsl.findPluginUsages(plugin);

            var pluginDetailView = new App.PluginDetailView({
                plugin: plugin,
                usages: usages
            });
            this.detailRegion.show(pluginDetailView);
        },

        loadSelectedDsl: function() {
            var url = $('.version-select').val();
            return this.dslLoader.fetch(url).then(this.onDslFetchComplete.bind(this));
        },

        onDslFetchComplete: function(dsl) {
            this.dsl = dsl;
            this.plugins = this.dsl.getPluginList();

            this.initTree();
            this.initPluginList();

            var allItems = [];
            _.forEach(this.dsl.getAllContexts(), function(context, clazz) {
                context.methods.forEach(function(method) {
                    allItems.push({
                        name: method.name,
                        clazz: clazz,
                        method: method,
                        simpleClassName: context.simpleClassName
                    });
                });
            });
            allItems = _.sortBy(allItems, function(item) { return item.name.toLowerCase(); });
            this.allItems = allItems;
        },

        initPluginList: function() {
            var pluginList = this.plugins;
            var pluginsView = new App.PluginsView({
                settings: this.settings,
                pluginList: pluginList
            });
            this.pluginsRegion.show(pluginsView);

            $('.plugins-header .checkbox-wrapper').click(function(e) { // TODO move to view
                e.stopPropagation();
                var $checkbox = $(e.currentTarget).find('input');
                if (!$(e.target).is('input')) {
                    $checkbox.prop('checked', !$checkbox.prop('checked'));
                }
                if ($checkbox.prop('checked')) {
                    this.settings.includeAllPlugins();
                } else {
                    this.settings.excludeAllPlugins(_.pluck(pluginList, 'name'));
                }
            }.bind(this));
        },

        onSearch: function() { // TODO move to view
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
                    return item.name.toLowerCase().indexOf(val.toLowerCase()) !== -1 &&
                        (!item.method.plugin || !this.settings.isPluginExcluded(item.method.plugin.name));
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
                east__paneSelector: '.plugins-wrapper',
                east__contentSelector: '.plugins-body',
                east__initClosed: true,
                east__size: 300,
                east__spacing_open: 3,
                east__resizerCursor: 'ew-resize',
                center__paneSelector: '.detail-wrapper',
                north__size: 50,
                resizable: true,
                closable: false,
                enableCursorHotkey: false
            });
        },

        initTree: function() {
            var treeView = new App.TreeView({
                settings: this.settings,
                dsl: this.dsl
            });
            this.treeRegion.show(treeView);
        },

        showPath: function(path) {
            var detailView = new App.DetailView({
                dsl: this.dsl,
                settings: this.settings,
                path: path
            });

            detailView.on('show render', function() {  // TODO move to view
                this.highlightCode($('.highlight'));
                $('.detail-wrapper').find('.expand-closure').click(this.onExpandClick.bind(this));
            }.bind(this));

            this.detailRegion.show(detailView);
        },

        showHome: function() {
            var homeView = new App.HomeView({
                settings: this.settings,
                dsl: this.dsl,
                plugins: this.plugins
            });

            homeView.on('show render', function() {  // TODO move to view
                this.highlightCode($('.highlight'));
                $('.detail-wrapper').find('.expand-closure').click(this.onExpandClick.bind(this));
            }.bind(this));

            this.detailRegion.show(homeView);
        },

        onExpandClick: function(e) {  // TODO move to view
            e.preventDefault();
            var $el = $(e.currentTarget);
            var path = $el.data('path');
            var index = $el.data('index');

            $el.hide();

            var pathInfo = this.dsl.getPathInfo(path);
            var parentSignature = pathInfo.methodNode.signatures[index];
            var signatures = this.dsl.getContextSignatures(parentSignature.contextClass, path);

            signatures = _.filter(signatures, function(sig) {
                return !sig.methodPlugin || !this.settings.isPluginExcluded(sig.methodPlugin.name);
            }, this);

            var contextView = new App.ContextView({signatures: signatures});
            var $contextHtml = contextView.render().$el;
            $contextHtml.insertAfter($el);

            this.highlightCode($contextHtml.find('.highlight'));

            $contextHtml.find('.expand-closure').click(this.onExpandClick.bind(this));
        },

        highlightCode: function($elements) {  // TODO move to view
            $elements.each(function(i, block) {
                hljs.highlightBlock(block);
                $(block).removeClass('ruby'); // TODO hljs bug?
            });
        }
    });

    $(function() { App.start(); });
}(jQuery));

/**
 * Provides access to DSL data.
 */
App.Dsl = function(data) {
    this.data = data;
    _.forEach(data.contexts, this._processContext.bind(this));
};
_.extend(App.Dsl.prototype, {

    _processContext: function(context) {
        var tokens = context.type.split('.');
        context.simpleClassName = tokens[tokens.length - 1];

        context.methods.forEach(function(method) {
            if (method.signatures.every(function(sig) { return sig.deprecated; })) {
                method.deprecated = true;
            }

            var signatureWithContext = _.find(method.signatures, function(signature) { return signature.contextClass && !signature.deprecated; });
            if (!signatureWithContext) {
                signatureWithContext = _.find(method.signatures, function(signature) { return signature.contextClass; });
            }

            if (signatureWithContext) {
                method.contextClass = signatureWithContext.contextClass;
            }

            var signatureWithPlugin = _.find(method.signatures, function(signature) { return signature.plugin; });
            if (signatureWithPlugin) {
                method.plugin = window.updateCenter.data.plugins[signatureWithPlugin.plugin.id];
            }
        });
    },

    isEmbedded: function() {
        return this.data.embedded;
    },

    getContext: function(contextClass) {
        return this.data.contexts[contextClass];
    },

    getRootContextClass: function() {
        return this.data.root.contextClass;
    },

    getPluginList: function() {
        return _.chain(this.data.contexts)
            .pluck('methods')
            .flatten()
            .pluck('plugin')
            .filter()
            .unique()
            .sortBy(function (item) {
                return item.title.toLowerCase();
            })
            .value();
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
        var contextNode = this.data.contexts[contextClass];

        for (var i = 0; i < tokens.length; i++) {
            var token = tokens[i];
            methodNode = _.findWhere(contextNode.methods, {name: token});

            if (i < tokens.length - 1) {
                contextNode = this.getContext(methodNode.contextClass);
                // TODO this is a hack to make sure we get the right context (for copyArtifacts). it only checks one level though.
                // should be a depth-first search or something
                var nextToken = tokens[i + 1];
                var matchingSig = _.find(methodNode.signatures, function(signature) {
                    var match = false;
                    var sigContextClass = signature.contextClass;
                    if (sigContextClass) {
                        var sigContext = this.getContext(sigContextClass);
                        match = !!_.findWhere(sigContext.methods, {name: nextToken});
                    }
                    return match;
                }, this);
                contextNode = this.getContext(matchingSig.contextClass);
            }
        }

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
        return method.signatures.map(function(signature, index) {

            if (signature.contextClass) {
                signature.context = this.data.contexts[signature.contextClass];
            }

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

            var data = {
                name: method.name,
                href: href,
                path: path,
                index: index,
                availableSince: signature.availableSince,
                deprecated: signature.deprecated,
                generated: signature.generated,
                extension: signature.extension,
                required: signature.required,
                text: text,
                html: signature.html,
                context: signature.context,
                comment: signature.firstSentenceCommentText
            };

            var enums = _.chain(signature.parameters)
                .filter(function(parameter) { return parameter.enumConstants; })
                .map(function(parameter) {
                    var typeTokens = parameter.type.split('.');
                    var simpleName = typeTokens[typeTokens.length - 1];
                    return {
                        paramName: parameter.name,
                        values: parameter.enumConstants.map(function(v) { return simpleName + '.' + v; })
                    };
                })
                .value();

            if (enums.length) {
                data.enums = enums;
            }

            data.methodPlugin = method.plugin;
            if (signature.plugin) {
                data.plugin = signature.plugin;
                var pluginData = window.updateCenter.data.plugins[signature.plugin.id];
                if (pluginData) {
                    data.plugin.title = pluginData.title;
                } else {
                    console.log('plugin not found', signature.plugin.id);
                }
            }

            return data;
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

            if (ancestors.length && methodIndex !== -1) {
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

/**
 * Loads and caches DSL data.
 */
App.DslLoader = function() {
    this.dslsByUrl = {};
};
_.extend(App.DslLoader.prototype, {

    fetch: function(url) {
        var dsl = this.dslsByUrl[url];
        if (!dsl) {
            return $.get(url).then(function(data) {
                var dsl = new App.Dsl(data);
                this.dslsByUrl[url] = dsl;
                return dsl;
            }.bind(this));
        }
        return $.Deferred().resolveWith(null, [dsl]);
    }
});

App.Router = Backbone.Router.extend({

    routes: {
        'path/:path':   'path',
        'method/:path': 'path',
        'plugin/:name': 'plugin',
        '*path':        'home'
    }
});

App.Settings = Marionette.Object.extend({

    initialize: function() {
        var parsed = JSON.parse(localStorage.getItem('job-dsl-api-viewer')) || {};
        this.excludedPlugins = parsed.excludedPlugins || [];
    },

    save: function() {
        localStorage.setItem('job-dsl-api-viewer', JSON.stringify({
            excludedPlugins: this.excludedPlugins
        }));
        this.trigger('change');
    },

    isPluginExcluded: function(name) {
        return _.contains(this.excludedPlugins, name);
    },

    setPluginExcluded: function(name, isExcluded) {
        if (isExcluded && !this.isPluginExcluded(name)) {
            this.excludedPlugins.push(name);
        }
        if (!isExcluded && this.isPluginExcluded(name)) {
            this.excludedPlugins = _.without(this.excludedPlugins, name);
        }
        this.save();
    },

    includeAllPlugins: function() {
        this.excludedPlugins = [];
        this.save();
    },

    excludeAllPlugins: function(names) {
        this.excludedPlugins = names;
        this.save();
    }
});

App.ContextView = Marionette.ItemView.extend({

    className: 'context-view',

    template: 'context',

    serializeData: function() {
        return {
            signatures: this.options.signatures
        };
    }
});

this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["context"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "        <li>\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.comment : depth0), {"name":"if","hash":{},"fn":this.program(2, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "            <a href=\"#path/"
    + escapeExpression(((helper = (helper = helpers.path || (depth0 != null ? depth0.path : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"path","hash":{},"data":data}) : helper)))
    + "\">"
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "</a><span class=\"highlight groovy inline\">"
    + escapeExpression(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"text","hash":{},"data":data}) : helper)))
    + "</span>\r\n            ";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.context : depth0), {"name":"if","hash":{},"fn":this.program(5, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "\r\n        </li>\r\n";
},"2":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "                <div class=\"firstSentenceCommentText\">\r\n                    // "
    + escapeExpression(((helper = (helper = helpers.comment || (depth0 != null ? depth0.comment : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"comment","hash":{},"data":data}) : helper)))
    + " ";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.deprecated : depth0), {"name":"if","hash":{},"fn":this.program(3, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "\r\n                </div>\r\n";
},"3":function(depth0,helpers,partials,data) {
  return "Deprecated.";
  },"5":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "{<span class=\"expand-closure glyphicon glyphicon-option-horizontal\" data-path=\""
    + escapeExpression(((helper = (helper = helpers.path || (depth0 != null ? depth0.path : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"path","hash":{},"data":data}) : helper)))
    + "\" data-index=\""
    + escapeExpression(((helper = (helper = helpers.index || (depth0 != null ? depth0.index : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"index","hash":{},"data":data}) : helper)))
    + "\"></span>}";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<ul class=\"inline-context-methods\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.signatures : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</ul>";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["detail"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "        <ol class=\"breadcrumb\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.ancestors : depth0), {"name":"each","hash":{},"fn":this.program(2, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "            <li class=\"active\">"
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "</li>\r\n        </ol>\r\n";
},"2":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "                <li><a href=\"#path/"
    + escapeExpression(((helper = (helper = helpers.id || (depth0 != null ? depth0.id : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"id","hash":{},"data":data}) : helper)))
    + "\">"
    + escapeExpression(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"text","hash":{},"data":data}) : helper)))
    + "</a></li>\r\n";
},"4":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "<a href=\""
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.methodNode : depth0)) != null ? stack1.plugin : stack1)) != null ? stack1.wiki : stack1), depth0))
    + "\"><span class=\"glyphicon glyphicon-new-window\"></span> "
    + escapeExpression(lambda(((stack1 = ((stack1 = (depth0 != null ? depth0.methodNode : depth0)) != null ? stack1.plugin : stack1)) != null ? stack1.title : stack1), depth0))
    + "</a>";
},"6":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "                ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.minimumVersion : stack1), {"name":"if","hash":{},"fn":this.program(7, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.availableSince : depth0), {"name":"if","hash":{},"fn":this.program(9, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.deprecated : depth0), {"name":"if","hash":{},"fn":this.program(11, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.generated : depth0), {"name":"if","hash":{},"fn":this.program(13, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.extension : depth0), {"name":"if","hash":{},"fn":this.program(15, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.required : depth0), {"name":"if","hash":{},"fn":this.program(17, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "                <div class=\"signature\">\r\n                    "
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "<span class=\"highlight groovy inline\">"
    + escapeExpression(((helper = (helper = helpers.text || (depth0 != null ? depth0.text : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"text","hash":{},"data":data}) : helper)))
    + "</span>\r\n                    ";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.context : depth0), {"name":"if","hash":{},"fn":this.program(19, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n                </div>\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.html : depth0), {"name":"if","hash":{},"fn":this.program(21, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.enums : depth0), {"name":"if","hash":{},"fn":this.program(23, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer;
},"7":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "<span class=\"label label-min-version\">Requires "
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.title : stack1), depth0))
    + " v"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.minimumVersion : stack1), depth0))
    + "+</span>";
},"9":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "                    <span class=\"label label-since\">Since "
    + escapeExpression(((helper = (helper = helpers.availableSince || (depth0 != null ? depth0.availableSince : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"availableSince","hash":{},"data":data}) : helper)))
    + "</span>\r\n";
},"11":function(depth0,helpers,partials,data) {
  return "                    <span class=\"label label-deprecated\"><a href=\"https://github.com/jenkinsci/job-dsl-plugin/wiki/Deprecation-Policy\" target=\"_blank\">Deprecated</a></span>\r\n";
  },"13":function(depth0,helpers,partials,data) {
  return "                    <span class=\"label label-generated\"><a href=\"https://github.com/jenkinsci/job-dsl-plugin/wiki/Automatically-Generated-DSL\" target=\"_blank\">Generated</a></span>\r\n";
  },"15":function(depth0,helpers,partials,data) {
  return "                    <span class=\"label label-extension\"><a href=\"https://github.com/jenkinsci/job-dsl-plugin/wiki/Extending-the-DSL\" target=\"_blank\">Extension</a></span>\r\n";
  },"17":function(depth0,helpers,partials,data) {
  return "                    <span class=\"label label-required\">Required</span>\r\n";
  },"19":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "{<span class=\"expand-closure glyphicon glyphicon-option-horizontal\" data-path=\""
    + escapeExpression(((helper = (helper = helpers.path || (depth0 != null ? depth0.path : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"path","hash":{},"data":data}) : helper)))
    + "\" data-index=\""
    + escapeExpression(((helper = (helper = helpers.index || (depth0 != null ? depth0.index : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"index","hash":{},"data":data}) : helper)))
    + "\"></span>}";
},"21":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, buffer = "                    <div class=\"method-doc\">";
  stack1 = ((helper = (helper = helpers.html || (depth0 != null ? depth0.html : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"html","hash":{},"data":data}) : helper));
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</div>\r\n";
},"23":function(depth0,helpers,partials,data) {
  var stack1, buffer = "                    <div class=\"enums\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.enums : depth0), {"name":"each","hash":{},"fn":this.program(24, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "                    </div>\r\n";
},"24":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "                        <div class=\"enum\">\r\n                            <div class=\"enum-title\">Possible values for <code>"
    + escapeExpression(((helper = (helper = helpers.paramName || (depth0 != null ? depth0.paramName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"paramName","hash":{},"data":data}) : helper)))
    + "</code>:</div>\r\n                            <ul>\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.values : depth0), {"name":"each","hash":{},"fn":this.program(25, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "                            </ul>\r\n                        </div>\r\n";
},"25":function(depth0,helpers,partials,data) {
  var lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "                                    <li>"
    + escapeExpression(lambda(depth0, depth0))
    + "</li>\r\n";
},"27":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "            <h3 class=\"section-header\">Examples</h3>\r\n\r\n            <pre class=\"highlight groovy\">"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.methodNode : depth0)) != null ? stack1.examples : stack1), depth0))
    + "</pre>\r\n";
},"29":function(depth0,helpers,partials,data) {
  var stack1, buffer = "            <h3 class=\"section-header\">Usages</h3>\r\n            <ul class=\"usages\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.usages : depth0), {"name":"each","hash":{},"fn":this.program(30, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "            </ul>\r\n";
},"30":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression, buffer = "                    <li>\r\n                        <div class=\"method-name ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.deprecated : stack1), {"name":"if","hash":{},"fn":this.program(31, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "\">\r\n                            <a href=\"#method/"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.context : depth0)) != null ? stack1.type : stack1), depth0))
    + "."
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "\" title=\""
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "\">"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "</a>\r\n                            : <span class=\"simple-class-name\">"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.context : depth0)) != null ? stack1.simpleClassName : stack1), depth0))
    + "</span>\r\n                        </div>\r\n                    </li>\r\n";
},"31":function(depth0,helpers,partials,data) {
  return "deprecated";
  },"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression, buffer = "<div class=\"detail\">\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.ancestors : depth0), {"name":"if","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "    <div class=\"method-detail\">\r\n        <h2>\r\n            "
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "\r\n            ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.methodNode : depth0)) != null ? stack1.plugin : stack1), {"name":"if","hash":{},"fn":this.program(4, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n        </h2>\r\n        <div class=\"signatures\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.signatures : depth0), {"name":"each","hash":{},"fn":this.program(6, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "        </div>\r\n\r\n";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.methodNode : depth0)) != null ? stack1.examples : stack1), {"name":"if","hash":{},"fn":this.program(27, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  buffer += "\r\n";
  stack1 = helpers['if'].call(depth0, (depth0 != null ? depth0.usages : depth0), {"name":"if","hash":{},"fn":this.program(29, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "    </div>\r\n</div>";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["home"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression;
  return "<div class=\"detail\">\r\n    <div class=\"method-detail\">\r\n        <h2>Jenkins Job DSL API</h2>\r\n\r\n        <div class=\"intro\">\r\n            <p>\r\n                Welcome to the Job DSL API Viewer. This is the Job DSL reference, showing all available DSL methods. Use the navigation\r\n                on the left to browse all methods starting from the methods available in the script context.\r\n            </p>\r\n            <p>\r\n                The Job DSL API currently supports "
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.plugins : depth0)) != null ? stack1.length : stack1), depth0))
    + " Jenkins plugins. Click the <span class=\"glyphicon glyphicon-filter\"></span>\r\n                on the top-right to filter methods by plugin.\r\n            </p>\r\n            <p>\r\n                For further documentation, please go to the <a href=\"https://github.com/jenkinsci/job-dsl-plugin/wiki\">Job DSL Wiki</a>.\r\n            </p>\r\n            <p>\r\n                Other Jenkins plugins can contribute DSL methods through extension points. Refer to the plugins'\r\n                wiki pages for documentation:\r\n            </p>\r\n            <ul>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/ClearCase+UCM+Plugin#ClearCaseUCMPlugin-JenkinsJobDSL\">ClearCase UCM Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/CodeSonar+Plugin#CodeSonarPlugin-JenkinsJobDSL\">CodeSonar Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/GitHub+pull+request+builder+plugin#GitHubpullrequestbuilderplugin-JobDSLSupport\">GitHub Pull Request Builder Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/JGiven+Plugin#JGivenPlugin-JobDSL\">JGiven Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Logging+Plugin#LoggingPlugin-JenkinsJobDSL\">Logging Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Memory+Map+Plugin#MemoryMapPlugin-JenkinsJobDSL\">Memory Map Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Next+Build+Number+Plugin#NextBuildNumberPlugin-JobDSL\">Next Build Number Plugin</a></li>\r\n                <li><a href=\"https://wiki.jenkins-ci.org/display/JENKINS/Pretested+Integration+Plugin#PretestedIntegrationPlugin-JenkinsJobDSL\">Pretested Integration Plugin</a></li>\r\n            </ul>\r\n        </div>\r\n\r\n        <h3 class=\"section-header\">Top-Level Methods</h3>\r\n        <div class=\"context-methods-section\"></div>\r\n    </div>\r\n</div>\r\n";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["pluginDetail"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression, buffer = "                <li>\r\n                    <div class=\"method-name ";
  stack1 = helpers['if'].call(depth0, ((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.deprecated : stack1), {"name":"if","hash":{},"fn":this.program(2, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "\">\r\n                        <a href=\"#method/"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.context : depth0)) != null ? stack1.type : stack1), depth0))
    + "."
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "\" title=\""
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "\">"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.method : depth0)) != null ? stack1.name : stack1), depth0))
    + "</a>\r\n                        : <span class=\"simple-class-name\">"
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.context : depth0)) != null ? stack1.simpleClassName : stack1), depth0))
    + "</span>\r\n                    </div>\r\n                </li>\r\n";
},"2":function(depth0,helpers,partials,data) {
  return "deprecated";
  },"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, lambda=this.lambda, escapeExpression=this.escapeExpression, buffer = "<div class=\"detail\">\r\n    <div class=\"method-detail\">\r\n        <h2>\r\n            "
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.title : stack1), depth0))
    + "\r\n\r\n            <a href=\""
    + escapeExpression(lambda(((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.wiki : stack1), depth0))
    + "\"><span class=\"glyphicon glyphicon-new-window\"></span> Wiki</a></h2>\r\n        </h2>\r\n        <div class=\"method-doc\">";
  stack1 = lambda(((stack1 = (depth0 != null ? depth0.plugin : depth0)) != null ? stack1.excerpt : stack1), depth0);
  if (stack1 != null) { buffer += stack1; }
  buffer += "</div>\r\n\r\n        <h3 class=\"section-header\">DSL Methods</h3>\r\n        <ul class=\"usages\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.usages : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "        </ul>\r\n    </div>\r\n</div>";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["plugins"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "        <li data-plugin-name=\""
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "\"><a href=\"#plugin/"
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "\"><span class=\"checkbox-wrapper\"><input type=\"checkbox\" checked /></span> "
    + escapeExpression(((helper = (helper = helpers.title || (depth0 != null ? depth0.title : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"title","hash":{},"data":data}) : helper)))
    + "</a></li>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<ul class=\"nav\">\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.plugins : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</ul>";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["searchResults"] = Handlebars.template({"1":function(depth0,helpers,partials,data) {
  var helper, functionType="function", helperMissing=helpers.helperMissing, escapeExpression=this.escapeExpression;
  return "        <li>\r\n            <a href=\"#method/"
    + escapeExpression(((helper = (helper = helpers.clazz || (depth0 != null ? depth0.clazz : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"clazz","hash":{},"data":data}) : helper)))
    + "."
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + "\">\r\n                <div>\r\n                    "
    + escapeExpression(((helper = (helper = helpers.name || (depth0 != null ? depth0.name : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"name","hash":{},"data":data}) : helper)))
    + " :\r\n                    <span class=\"simple-class-name\">"
    + escapeExpression(((helper = (helper = helpers.simpleClassName || (depth0 != null ? depth0.simpleClassName : depth0)) != null ? helper : helperMissing),(typeof helper === functionType ? helper.call(depth0, {"name":"simpleClassName","hash":{},"data":data}) : helper)))
    + "</span>\r\n                </div>\r\n            </a>\r\n        </li>\r\n";
},"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  var stack1, buffer = "<ul>\r\n";
  stack1 = helpers.each.call(depth0, (depth0 != null ? depth0.results : depth0), {"name":"each","hash":{},"fn":this.program(1, data),"inverse":this.noop,"data":data});
  if (stack1 != null) { buffer += stack1; }
  return buffer + "</ul>";
},"useData":true});
this["Handlebars"] = this["Handlebars"] || {};
this["Handlebars"]["templates"] = this["Handlebars"]["templates"] || {};
this["Handlebars"]["templates"]["tree"] = Handlebars.template({"compiler":[6,">= 2.0.0-beta.1"],"main":function(depth0,helpers,partials,data) {
  return "<div class=\"tree-body\"></div>\r\n<div class=\"search-results\" style=\"display: none\"></div>";
  },"useData":true});
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

App.HomeView = Marionette.LayoutView.extend({

    template: 'home',

    regions: {
        contextRegion: '.context-methods-section'
    },

    initialize: function(options) {
        this.settings = options.settings;
        this.dsl = options.dsl;
        this.plugins = options.plugins;
        this.listenTo(this.settings, 'change', this.render);
    },

    onRender: function() {
        var pathInfo = this.dsl.getPathInfo();
        var methodNode = pathInfo.methodNode;
        var signatures = this.dsl.getContextSignatures(methodNode.contextClass);
        signatures = _.filter(signatures, function(sig) {
            return !sig.plugin || !this.settings.isPluginExcluded(sig.plugin.id);
        }, this);
        var contextView = new App.ContextView({signatures: signatures});
        this.contextRegion.show(contextView);
    },

    serializeData: function() {
        return {plugins: this.plugins};
    }
});

App.PluginDetailView = Marionette.ItemView.extend({

    template: 'pluginDetail',

    serializeData: function() {
        return {
            plugin: this.options.plugin,
            usages: this.options.usages
        };
    }
});

App.PluginsView = Marionette.ItemView.extend({

    template: 'plugins',

    events: {
        'click .checkbox-wrapper': 'onWrapperClick'
    },

    initialize: function(options) {
        this.settings = options.settings;
        this.pluginList = options.pluginList;
        this.listenTo(this.settings, 'change', this.onRender);
    },

    onRender: function() {
        this.$('li').each(function(index, el) {
            var $el = $(el);
            var name = $el.data('pluginName');
            var checked = !this.settings.isPluginExcluded(name);
            $el.find('input').prop('checked', checked);
        }.bind(this));
    },

    onWrapperClick: function(e) {
        e.stopPropagation();
        var $checkbox = $(e.currentTarget).find('input');
        var name = $checkbox.closest('li').data('pluginName');
        if (!$(e.target).is('input')) {
            $checkbox.prop('checked', !$checkbox.prop('checked'));
        }
        this.settings.setPluginExcluded(name, !$checkbox.prop('checked'));
    },

    serializeData: function() {
        var pluginList = this.pluginList;
        return {
            numPlugins: pluginList.length,
            plugins: pluginList
        };
    }
});

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
