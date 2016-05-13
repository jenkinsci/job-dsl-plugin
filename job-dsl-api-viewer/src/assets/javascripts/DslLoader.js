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
