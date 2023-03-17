import Marionette from "backbone.marionette";
import Dsl from "./Dsl";

/**
 * Loads and caches DSL data.
 */
export default Marionette.Object.extend({
    initialize: function() {
        this.dslsByUrl = {};
    },

    fetch: function(url) {
        var dsl = this.dslsByUrl[url];
        if (!dsl) {
            return $.get(url).then(function(data) {
                var dsl = new Dsl({ data: data });
                this.dslsByUrl[url] = dsl;
                return dsl;
            }.bind(this));
        }
        return $.Deferred().resolveWith(null, [dsl]);
    }
});
