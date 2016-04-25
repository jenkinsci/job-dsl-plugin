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
