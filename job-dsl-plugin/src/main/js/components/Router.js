import Backbone from 'backbone';

export default Backbone.Router.extend({
    routes: {
        'path/:path':   'path',
        'method/:path': 'path',
        'plugin/:name': 'plugin',
        '*path':        'home'
    }
});
