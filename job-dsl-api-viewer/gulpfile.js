var gulp        = require('gulp');
var handlebars  = require('gulp-handlebars');
var wrap        = require('gulp-wrap');
var declare     = require('gulp-declare');
var concat      = require('gulp-concat');
var less        = require('gulp-less');
var path        = require('path');
var minifyCss   = require('gulp-minify-css');
var connect     = require('gulp-connect');
var watch       = require('gulp-watch');
var del         = require('del');
var merge       = require('merge-stream');
var request     = require('request');
var source      = require('vinyl-source-stream');

gulp.task('templates', function(){
    var templates = gulp.src('./src/templates/*.hbs')
        .pipe(handlebars())
        .pipe(wrap('Handlebars.template(<%= contents %>)'))
        .pipe(declare({
            namespace: 'Handlebars.templates'
        }))
        .pipe(concat('templates.js'));

    var js = gulp.src('./src/js/**/*.js');

    return merge(js, templates)
        .pipe(concat('app.js'))
        .pipe(gulp.dest('./build/dist/js/'));
});

gulp.task('less', function () {
    return gulp.src('./src/css/**/*.less')
        .pipe(less())
        .pipe(minifyCss({keepBreaks:true}))
        .pipe(concat('app.css'))
        .pipe(gulp.dest('./build/dist/css/'));
});

gulp.task('watch', function () {
    gulp.watch(['./src/**/*', 'gulpfile.js'], ['build']);
});

gulp.task('connect', ['watch'], function() {
    connect.server();
});

gulp.task('download', function () {
    return request('http://updates.jenkins-ci.org/update-center.json')
        .pipe(source('update-center.jsonp'))
        .pipe(gulp.dest('./build/data/'));
});

gulp.task('clean', function() {
    del.sync(['build']);
});

gulp.task('build', ['clean', 'download', 'templates', 'less']);

gulp.task('default', ['build']);