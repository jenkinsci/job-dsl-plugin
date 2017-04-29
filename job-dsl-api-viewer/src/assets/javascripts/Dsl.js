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
            method.signatures = _.reject(method.signatures, function(signature){ return !window.App.config.embedded && signature.embeddedOnly; });
            method.signatures.forEach(function (signature) {
                // maintain compatibility with older data
                if (signature.plugin) {
                    signature.plugins = [signature.plugin];
                } else {
                    signature.plugins = signature.plugins || [];
                }
            });

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

            method.plugins = _.chain(method.signatures)
                .pluck('plugins')
                .flatten()
                .map(function(plugin) {
                    var data = window.updateCenter.data.plugins[plugin.id];
                    if (!data) {
                        data = {
                            name: plugin.id,
                            title: plugin.id
                        };
                    }
                    return data;
                })
                .value();
        });
        context.methods = _.reject(context.methods, function(method){ return method.signatures.length == 0; });
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
            .pluck('plugins')
            .flatten()
            .unique(false, function(item) { return item.name; })
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
                method.plugins.forEach(function(methodPlugin) {
                    if (methodPlugin === plugin) {
                        usages.push({method: method, context: context});
                    }
                });
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
            var deprecatedText = signature.deprecatedText;
            if (deprecatedText && deprecatedText.substring(deprecatedText.length - 1) === '.') {
                deprecatedText = deprecatedText.substring(0, deprecatedText.length - 1);
            }

            var data = {
                name: method.name,
                href: href,
                path: path,
                index: index,
                availableSince: signature.availableSince,
                deprecated: signature.deprecated,
                deprecatedText: deprecatedText,
                deprecatedHtml: signature.deprecatedHtml || deprecatedText,
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
                        values: parameter.enumConstants.map(function(v) { return parameter.type === 'String' ? '\'' + v + '\'' : simpleName + '.' + v; })
                    };
                })
                .value();

            if (enums.length) {
                data.enums = enums;
            }

            data.methodPlugins = method.plugins;
            if (signature.plugins) {
                data.plugins = signature.plugins;
                data.plugins.forEach(function (plugin) {
                    plugin.name = plugin.id;

                    var pluginData = window.updateCenter.data.plugins[plugin.id];
                    if (pluginData) {
                        plugin.title = pluginData.title;
                        plugin.wiki = pluginData.wiki;
                    } else {
                        plugin.title = plugin.id;
                    }
                });
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
