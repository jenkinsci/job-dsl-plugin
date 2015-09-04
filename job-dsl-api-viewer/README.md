# Job DSL API Viewer

## Development Setup 
1. Change to the `job-dsl-api-viewer` directory.
2. Install project dependencies with `npm install`.
3. Build the resources with `gulp build`.
4. Run the server with `gulp connect`. This will also watch for file changes.

## Publishing in gh-pages
1. Change to the project root directory.
2. Run `./gradlew :job-dsl-api-viewer:publishGhPages`
3. To publish to a fork, pass a githubUser param like `./gradlew -PgithubUser=sheehan :job-dsl-api-viewer:publishGhPages`