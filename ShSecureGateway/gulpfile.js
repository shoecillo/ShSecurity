
var gulp = require('gulp');

gulp.task('copy-js-libs', function() {
	gulp.src([ './node_modules/angular/angular.min.js',
		'./node_modules/bootstrap/dist/js/bootstrap.min.js',
		'./node_modules/jquery/dist/jquery.min.js',
		'./node_modules/angular-ui-router/release/angular-ui-router.min.js' ])
		.pipe(gulp.dest('./src/main/resources/static/jsLib'));
});

gulp.task('copy-css', function() {
	gulp.src([ './node_modules/bootstrap/dist/css/bootstrap.css',
		'./node_modules/bootstrap/dist/css/bootstrap.css.map',
		'./node_modules/bootstrap/dist/css/bootstrap-theme.css',
		'./node_modules/bootstrap/dist/css/bootstrap-theme.css.map',
		'./node_modules/font-awesome/css/font-awesome.min.css' ])
		.pipe(gulp.dest('.//src/main/resources/static/css'));
});

gulp.task('copy-fonts', function() {
	gulp.src([ './node_modules/bootstrap/dist/fonts/**/*',
		'./node_modules/font-awesome/fonts/**/*' ])
		.pipe(gulp.dest('.//src/main/resources/static/fonts'));
});

gulp.task('copy-libs', [ 'copy-js-libs', 'copy-css', 'copy-fonts' ]);