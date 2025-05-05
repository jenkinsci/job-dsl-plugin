import 'bootstrap/dist/css/bootstrap.css';
import 'highlight.js/styles/default.css';
import 'jquery.ui.layout';
import 'jstree';
import 'jstree/dist/themes/default/style.css';
import Backbone from 'backbone';
import { Application } from 'backbone.marionette';
import _ from 'underscore';
import groovy from 'highlight.js/lib/languages/groovy';
import hljs from 'highlight.js/lib/core';

import '../../less/main.less';
import ContextView from '../views/ContextView.js';
import DetailView from '../views/DetailView.js';
import DslLoader from './DslLoader.js';
import HomeView from '../views/HomeView.js';
import PluginDetailView from '../views/PluginDetailView.js';
import PluginsView from '../views/PluginsView.js';
import RootView from '../views/RootView.js';
import Router from './Router.js';
import SearchResults from '../../hbs/searchResults.hbs';
import Settings from './Settings.js';
import TreeView from '../views/TreeView.js';

    export default Application.extend({
        region: "#root",

        onStart: function() {
            this.dslLoader = new DslLoader();
            this.settings = new Settings();
            this.settings.on('change', this.highlightFilter, this);

            hljs.registerLanguage("groovy", groovy);

            this.router = new Router();
            this.router.on('route:home',    this.showHome, this);
            this.router.on('route:path',    this.showPath, this);
            this.router.on('route:method',  this.showPath, this);
            this.router.on('route:plugin',  this.showPlugin, this);

            var rootView = new RootView();
            this.showView(rootView);

            this.initLayout();
            $('.loading-outer').addClass('loading');
            this.loadConfig().then(this.loadUpdateCenter.bind(this)).then(this.loadSelectedDsl.bind(this)).then(function() {
                Backbone.history.start({pushState: false});
                if (!this.config.embedded) {
                    $('.version-select').show()
                }
            }.bind(this));

            $('.version-select').change(function() {
                this.loadSelectedDsl().then(function() {
                    Backbone.history.loadUrl(Backbone.history.getHash());
                });
            }.bind(this));
            $('.toggle-plugins').click(function() {
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

            this.highlightFilter();
        },

        showPlugin: function(name) {
            var plugin = _.findWhere(this.plugins, {name: name});
            var usages = this.dsl.findPluginUsages(plugin);

            var pluginDetailView = new PluginDetailView({
                plugin: plugin,
                usages: usages
            });
            this.getView().showChildView("detailRegion", pluginDetailView);
        },

        loadConfig: function() {
            var url = 'config.json';
            return $.get(url).then(function (data) {
              this.config = window.config = data;
              return data;
            }.bind(this));
        },

        loadUpdateCenter: function() {
            var url = this.config.embedded ? '../../../job-dsl-api-viewer/plugins' : 'update-center.json';
            return $.get(url).then(function (data) {
                window.updateCenter = {data: data};
                return data;
            }.bind(this));
        },

        loadSelectedDsl: function() {
            var url = this.config.embedded ? '../../../job-dsl-api-viewer/data' : $('.version-select').val();
            return this.dslLoader.fetch(url).then(this.onDslFetchComplete.bind(this), this.onDslFetchFailure.bind(this));
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

        onDslFetchFailure: function() {
            $('.loading-inner').html('Error while loading data, see Jenkins logs for details.');
        },

        initPluginList: function() {
            var pluginList = this.plugins;
            var pluginsView = new PluginsView({
                settings: this.settings,
                pluginList: pluginList
            });
            this.getView().showChildView("pluginRegion", pluginsView);

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
                        !this.settings.isPluginsExcluded(item.method.plugins);
                }, this);
                var html = SearchResults({ results: matches });
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
            var treeView = new TreeView({
                settings: this.settings,
                dsl: this.dsl
            });
            this.getView().showChildView("treeRegion", treeView);
        },

        showPath: function(path) {
            var detailView = new DetailView({
                application: this,
                dsl: this.dsl,
                settings: this.settings,
                path: path
            });

            this.getView().showChildView("detailRegion", detailView);
        },

        showHome: function() {
            var homeView = new HomeView({
                application: this,
                config: this.config,
                settings: this.settings,
                dsl: this.dsl,
                plugins: this.plugins
            });

            this.getView().showChildView("detailRegion", homeView);
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
                return !this.settings.isPluginsExcluded(sig.methodPlugins);
            }, this);

            var contextView = new ContextView({ signatures: signatures });
            var $contextHtml = contextView.render().$el;
            $contextHtml.insertAfter($el);

            this.highlightCode($contextHtml.find('.highlight'));

            $contextHtml.find('.expand-closure').click(this.onExpandClick.bind(this));
        },

        highlightCode: function($elements) {  // TODO move to view
            $elements.each(function(i, el) {
                hljs.highlightElement(el);
            });
        },

        highlightFilter: function () {
            if (this.settings.isPluginExcluded()) {
                $('.filter-active').removeClass('invisible')
            } else {
                $('.filter-active').addClass('invisible')
            }
        }
    });
